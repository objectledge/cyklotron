insert into ledge_scheduler values (1, 'cms.poll.checkstate',
  'cron','* * * * *', 'net.cyklotron.cms.modules.jobs.poll.CheckPollState',
  '',1,-1,now(),null,null,0,0,0);

insert into ledge_scheduler values (2, 'cms.link.checkstate',
  'cron','* * * * *', 'net.cyklotron.cms.modules.jobs.link.CheckLinkState',
  '',1,-1,now(),null,null,0,0,0);

insert into ledge_scheduler values (3, 'cms.banner.checkstate',
  'cron','* * * * *', 'net.cyklotron.cms.modules.jobs.banner.CheckBannerState',
  '',1,-1,now(),null,null,0,0,0);

insert into ledge_scheduler values (4, 'cms.forum.checkstate',
  'cron','* * * * *', 'net.cyklotron.cms.modules.jobs.forum.CheckModeratorTasks',
  '',1,-1,now(),null,null,0,0,0);  

insert into ledge_scheduler values (5, 'cms.httpfeed.updatefeeds',
  'cron','* * * * *', 'net.cyklotron.cms.modules.jobs.httpfeed.UpdateFeeds',
  '',1,-1,now(),null,null,0,0,0);  
  
insert into ledge_scheduler values (6, 'cms.periodicals.process',
  'cron','* * * * *', 'net.cyklotron.cms.modules.jobs.periodicals.ProcessPeriodicals',
  '',1,-1,now(),null,null,0,0,0);  
  
insert into ledge_scheduler values (7, 'cms.search.manageindexes',
  'cron','* * * * *', 'net.cyklotron.cms.modules.jobs.search.ManageIndexes',
  '',1,-1,now(),null,null,0,0,0);  
  
insert into ledge_scheduler values (8, 'cms.structure.checkstate',
  'cron','* * * * *', 'net.cyklotron.cms.modules.jobs.structure.CheckNodeState',
  '',1,-1,now(),null,null,0,0,0);  
  
  