\COPY (SELECT c.caseref FROM casesvc.case c WHERE EXISTS (SELECT 1 FROM casesvc.caseevent ce WHERE c.caseid = ce.caseid AND ce.category = 'IAC_AUTHENTICATED') AND NOT EXISTS (SELECT 1 FROM casesvc.caseevent ce WHERE c.caseid = ce.caseid AND ce.category = 'ONLINE_QUESTIONNAIRE_RESPONSE')) TO '/var/partial_responses/caserefs_to_flush.csv';
