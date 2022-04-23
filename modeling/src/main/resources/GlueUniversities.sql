with mirea as (
    select generalname,
           region,
           hostel,
           yearofdata,
           averageAllStudentsEGE,
           dolyaOfflineEducation,
           averagedMinimalEGE,
           averageBudgetEGE,
           countVserosBVI,
           countOlimpBVI,
           countCelevoiPriem,
           dolyaCelevoiPriem,
           ydelniyVesInostrancyWithoutSNG,
           ydelniyVesInostrancySNG,
           averageBudgetWithoutSpecialRightsEGE,
           datasource,
           jsonYGSN
    from university
             join nameuniversitiesmirea n on university.name = n.name
    where university.datasource = 'MIREA'
),
--      hse as (
--          select generalname,
--                 yearofdata,
--                 averagescorebudgetege,
--                 averagescorepaidege,
--                 growthdeclineaveragescorebudgetege,
--                 growthdeclineaveragescorepaidege,
--                 numbersbudgetstudents,
--                 numberspaidstudents,
--                 numbersstudentwithoutexam,
--                 datasource
--          from university
--                   join nameuniversitieshse n on university.name = n.name
--          where university.datasource = 'HSE'
-- ),

     glue_universities as (
         select mirea.generalname as university, mirea.region as region, mirea.hostel as hostel, mirea.yearofdata as data,
                mirea.averageAllStudentsEGE as average_all_students_ege, mirea.dolyaOfflineEducation as dolya_offline_education,
                mirea.averagedMinimalEGE as averaged_minimal_ege, mirea.averageBudgetEGE as average_budget_ege,
                mirea.countVserosBVI as count_vseros_bvi, mirea.countOlimpBVI as count_olimp_bvi,
                mirea.countCelevoiPriem as count_celevoi_priem, mirea.dolyaCelevoiPriem as dolya_celevoi_priem,
                mirea.ydelniyVesInostrancyWithoutSNG as ydelniy_ves_inostrancy_without_sng,
                mirea.ydelniyVesInostrancySNG as ydelniy_ves_inostrancy_sng,
                mirea.averageBudgetWithoutSpecialRightsEGE as average_budget_without_special_rights_ege,
                mirea.jsonYGSN as json_ygsn

         from mirea
         where mirea.generalname != ''
),

     ygsn_info as (
         select generalname as university,
                yearofdata as data,
                ygsnname as ygsn_name,
                averageScoreBudgetEGE as average_score_budget_ege_ygsn,
                averageScorePaidEGE as average_score_paid_ege_ygsn,
                numbersBudgetStudents as numbers_budget_students_ygsn,
                numbersPaidStudents as numbers_paid_students_ygsn
         from universityygsn
                  join nameuniversitieshse n on universityygsn.universityname = n.name
)
insert into statistics_universities_ygsn
select glue_universities.university, ygsn_info.ygsn_name, glue_universities.data,
       glue_universities.region, glue_universities.hostel,
       glue_universities.average_all_students_ege, glue_universities.dolya_offline_education,
       glue_universities.averaged_minimal_ege, glue_universities.average_budget_ege,
       glue_universities.count_vseros_bvi, glue_universities.count_olimp_bvi,
       glue_universities.count_celevoi_priem, glue_universities.dolya_celevoi_priem,
       glue_universities.ydelniy_ves_inostrancy_without_sng, glue_universities.ydelniy_ves_inostrancy_sng,
       glue_universities.average_budget_without_special_rights_ege, ygsn_info.average_score_budget_ege_ygsn,
       ygsn_info.average_score_paid_ege_ygsn, ygsn_info.numbers_paid_students_ygsn,
       ygsn_info.numbers_budget_students_ygsn, glue_universities.json_ygsn
from glue_universities
    join ygsn_info on glue_universities.university = ygsn_info.university and glue_universities.data = ygsn_info.data;





