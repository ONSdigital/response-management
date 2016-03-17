set schema 'action';

-- Function: action.createactions(integer,integer)
-- DROP FUNCTION action.createactions(integer,integer);

CREATE OR REPLACE FUNCTION action.createactions(p_actionplanjobid integer, p_actionplanid integer)
  RETURNS boolean AS
$BODY$
DECLARE
r_actionrule record;
r_actioncase record;
v_text text;
v_rule_cnt integer;
v_survey_name text;
v_rule_due_cnt integer;
v_total_rules integer;
v_errmess text;
BEGIN
   
   -- Check actionplanid exist on actionplanid passed in exists on the actionplan table
   IF EXISTS (SELECT 1 FROM action.actionplan WHERE actionplanid = p_actionplanid) THEN  
   
      -- Check cases for the actionplanjobid passed in exist on the case table
      IF EXISTS (SELECT 1 FROM action.case WHERE actionplanjobid = p_actionplanjobid) THEN  
          v_rule_due_cnt:=0;
          v_total_rules :=0;

          -- Get the survey name using the actionplanid passed in
          SELECT s.name 
          FROM action.survey s
              ,action.actionplan a 
          WHERE a.surveyid = s.surveyid
          AND a.actionplanid = p_actionplanid INTO v_survey_name;

          v_text := '********* SURVEY NAME: ' || v_survey_name || ' ********* RUNNING JOBID: ' ||p_actionplanjobid || ' ACTION PLAN: ' || p_actionplanid || ' *********';
          PERFORM action.logmessage(p_messagetext := v_text
                                   ,p_jobid := p_actionplanjobid    
                                   ,p_messagelevel := 'INFO'                                                  
                                   ,p_functionname := 'action.createactions');       
   
          -- Check each actionrule for the action plan passed in to see if it is due
          FOR r_actionrule IN (SELECT s.surveyid
                                     ,s.name
                                     ,p.actionplanid
                                     ,p.description as plan_desc
                                     ,r.actionruleid
                                     ,r.description as rule_desc
                                     ,t.actiontypename
                                     ,t.description as type_desc
                                     ,current_timestamp today
                                     ,s.surveydate
                                     ,r.surveydatedaysoffset
                                     ,EXTRACT(DAY FROM (current_timestamp - s.surveydate)) days_from_survey
                               FROM action.actionrule r
                                   ,action.actionplan p
                                   ,action.survey s
                                   ,action.actiontype t
                               WHERE  p.actionplanid = p_actionplanid
                               AND s.surveyid = p.surveyid
                               AND p.actionplanid = r.actionplanid
                               AND t.actiontypename = r.actiontypename 
                               AND r.surveydatedaysoffset <= EXTRACT(DAY FROM (current_timestamp - s.surveydate))) LOOP

              v_rule_due_cnt := v_rule_due_cnt + 1;
            
              -- Report on the Action Plan and Action Rule due
              v_text := 'RULE DUE: ' ||  r_actionrule.actionruleid || ' (Survey Days Offset = ' || r_actionrule.surveydatedaysoffset 
                        || ', Current Days From Survey = ' ||    r_actionrule.days_from_survey|| ')' || ' Rule Description: ' || r_actionrule.rule_desc 
                        || ', Type Description: ' || r_actionrule.type_desc;
              PERFORM action.logmessage(p_messagetext := v_text
                                       ,p_jobid := p_actionplanjobid    
                                       ,p_messagelevel := 'INFO'                                                  
                                       ,p_functionname := 'action.createactions');

              v_rule_cnt:= 0;   
        
              FOR r_actioncase IN ( -- Select the cases from the case table for the actionplanjobid - case to process
                                   SELECT c.caseid, p_actionplanid, r_actionrule.actionruleid
                                   FROM  action.case c 
                                   WHERE c.actionplanjobid = p_actionplanjobid 
                                   EXCEPT
	   		           -- Remove from the result set any cases where the case action has already been created in the action table where the
	   		           -- actionplanid and actionruleid match. 
                                   -- If a case has already been created previously but for a different plan it will be included
                                   -- this is to allow for a case record to change plans
                                   SELECT c.caseid, a.actionplanid, a.actionruleid
                                   FROM action.action a
                                       ,action.case c 
                                   WHERE a.actionplanid  = p_actionplanid
                                   AND a.actionruleid    = r_actionrule.actionruleid
                                   AND c.actionplanjobid = p_actionplanjobid
                                   AND c.caseid = a.caseid) LOOP                 
                 INSERT INTO action.action
                          (actionid
                          ,caseid 
                          ,actionplanid 
                          ,actionruleid
                          ,actiontypename 
                          ,createdby 
                          ,manuallycreated
                          ,priority
                          ,situation
                          ,state
                          ,createddatetime 
                          ,updateddatetime )
                 SELECT  nextval('action.actionidseq')
                          ,r_actioncase.caseid
                          ,p_actionplanid
                          ,r_actioncase.actionruleid
                          ,r_actionrule.actiontypename
                          ,'SYSTEM'
                          ,FALSE
                          ,0
                          ,NULL
                          ,'SUBMITTED'
                          ,current_timestamp
                          ,NULL;

                 v_rule_cnt := v_rule_cnt + 1;
                 v_total_rules := v_total_rules + 1;
               END LOOP;           
          
              IF v_rule_cnt = 0 THEN 
                 v_text := 'NO NEW CASE ACTION(S) CREATED FOR PLAN: ' || p_actionplanid || ' RULE: ' || r_actionrule.actionruleid;
              ELSE
                 v_text := v_rule_cnt  || ' NEW CASE ACTION(S) CREATED FOR PLAN: ' || p_actionplanid || ' RULE: ' || r_actioncase.actionruleid;
              END IF;
              PERFORM action.logmessage(p_messagetext := v_text
                                       ,p_jobid := p_actionplanjobid    
                                       ,p_messagelevel := 'INFO'                                                  
                                       ,p_functionname := 'action.createactions');     
          END LOOP;
                    
        
          IF v_rule_due_cnt = 0 THEN 
            v_text := '******* NO RULES DUE 0 NEW CASE ACTION(S) CREATED FOR JOBID: ' || p_actionplanjobid || ' ACTION PLAN: ' || p_actionplanid || ' *******' ;
          ELSE   
             -- Report TOTAL rules created for action plan
             v_text := '******* ' || v_total_rules || ' TOTAL NEW CASE ACTION(S) CREATED FOR JOBID: ' || p_actionplanjobid || ' ACTION PLAN: ' || p_actionplanid || ' *******' ;
          END IF;

          PERFORM action.logmessage(p_messagetext := v_text
                                   ,p_jobid := p_actionplanjobid    
                                   ,p_messagelevel := 'INFO'                                                  
                                   ,p_functionname := 'action.createactions'); 

         -- Update the date the actionplan was run on the actionplan table 
         UPDATE action.actionplan 
         SET lastgoodrundatetime = current_timestamp
         WHERE actionplanid = p_actionplanid;  

         v_text := '********* JOBID: ' || p_actionplanjobid || ' ACTION PLAN: ' || p_actionplanid || ' COMPLETE ********* (action.actionplan table updated with lastgoodrundatetime)'; 
         PERFORM action.logmessage(p_messagetext := v_text
                                   ,p_jobid := p_actionplanjobid    
                                   ,p_messagelevel := 'INFO'                                                  
                                   ,p_functionname := 'action.createactions'); 

         -- Update the date the actionplan was run on the actionplanjob table 
         UPDATE action.actionplanjob 
         SET updateddatetime = current_timestamp
             ,state = 'COMPLETED'
         WHERE actionplanjobid =  p_actionplanjobid
         AND   actionplanid    = p_actionplanid;  

         v_text := '********* JOBID: ' || p_actionplanjobid || ' ACTION PLAN: ' || p_actionplanid || ' COMPLETE ********* (action.actionplanjob table updated with updateddatetime)'; 
         PERFORM action.logmessage(p_messagetext := v_text
                                   ,p_jobid := p_actionplanjobid    
                                   ,p_messagelevel := 'INFO'                                                  
                                   ,p_functionname := 'action.createactions'); 

      ELSE
         v_text := '**** NO RECORDS FOUND ON action.case TABLE FOR JOBID: ' ||p_actionplanjobid || ' ****';
         PERFORM action.logmessage(p_messagetext := v_text
                                  ,p_jobid := p_actionplanjobid    
                                  ,p_messagelevel := 'ERROR'                                                  
                                  ,p_functionname := 'action.createactions');
      END IF;      

   ELSE
      v_text := '**** ACTION PLAN: '|| p_actionplanid || ' NOT FOUND ON action.actionplan TABLE ****';
      PERFORM action.logmessage(p_messagetext := v_text
                               ,p_jobid := p_actionplanjobid    
                               ,p_messagelevel := 'ERROR'                                                  
                               ,p_functionname := 'action.createactions');
   END IF;    

     
return true;

EXCEPTION

 WHEN OTHERS THEN
    v_errmess := SQLSTATE;    
    PERFORM action.logmessage(p_messagetext := 'CREATE ACTION(S) EXCEPTION TRIGGERED SQLERRM: ' || SQLERRM || ' SQLSTATE : ' || v_errmess
                             ,p_jobid := p_actionplanjobid   
                             ,p_messagelevel := 'FATAL'
                             ,p_functionname := 'action.createactions');
  return false;    
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION action.createactions(integer, integer)
  OWNER TO postgres;