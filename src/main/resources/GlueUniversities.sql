with mirea as (
    select generalname,
           yearofdata,
           researchactivities,
           internationalactivity,
           financialandeconomicactivities,
           salaryppp,
           additionalindicator,
           datasource
    from university
             join nameuniversitiesmirea n on university.name = n.name
    where university.datasource = 'MIREA'
),
     hse as (
         select generalname,
                yearofdata,
                averagescorebudgetege,
                averagescorepaidege,
                growthdeclineaveragescorebudgetege,
                growthdeclineaveragescorepaidege,
                numbersbudgetstudents,
                numberspaidstudents,
                numbersstudentwithoutexam,
                datasource
         from university
                  join nameuniversitieshse n on university.name = n.name
         where university.datasource = 'HSE'
),

     glue_universities as (
         select mirea.generalname as university, mirea.yearofdata as data, mirea.researchactivities as
                research_activities, mirea.internationalactivity as international_activity,
                mirea.financialandeconomicactivities as financial_and_economic_activities,
                mirea.salaryppp as salary_ppp, mirea.additionalindicator as additional_indicator,
                hse.averagescorebudgetege as average_score_budget_ege, hse.averagescorepaidege as average_score_paid_ege,
                hse.growthdeclineaveragescorebudgetege as growth_score_budget_ege, hse.growthdeclineaveragescorepaidege as
                growth_score_paid_ege, hse.numbersbudgetstudents as numbers_budget_students,
                hse.numberspaidstudents as numbers_paid_students, hse.numbersstudentwithoutexam as numbers_student_without_exam
         from mirea
                  join hse on mirea.generalname = hse.generalname and mirea.yearofdata = hse.yearofdata
         where mirea.generalname!= '' and hse.generalname != ''
),

     ygsn_info as (
         select generalname as university,
                yearofdata as data,
                ygsnname as ygsn_name,
                averagescorebudgetege as average_score_budget_ege_ygsn,
                averagescorepaidege as average_score_paid_ege_ygsn,
                growthdeclineaveragescorebudgetege as growth_score_budget_ege_ygsn,
                growthdeclineaveragescorepaidege as growth_score_paid_ege_ygsn,
                numbersbudgetstudents as numbers_budget_students_ygsn,
                numberspaidstudents as numbers_paid_students_ygsn,
                numbersstudentwithoutexam as numbers_student_without_exam_ygsn,
                costeducation as cost_education_ygsn
         from universityygsn
                  join nameuniversitieshse n on universityygsn.universityname = n.name
)
insert into statistics_universities_ygsn
select glue_universities.university, ygsn_info.ygsn_name, glue_universities.data,
       glue_universities.research_activities, glue_universities.international_activity,
       glue_universities.financial_and_economic_activities, glue_universities.salary_ppp,
       glue_universities.additional_indicator, glue_universities.average_score_budget_ege,
       glue_universities.average_score_paid_ege, glue_universities.growth_score_budget_ege,
       glue_universities.growth_score_paid_ege, glue_universities.numbers_budget_students,
       glue_universities.numbers_paid_students, glue_universities.numbers_student_without_exam,
       ygsn_info.average_score_budget_ege_ygsn, ygsn_info.average_score_paid_ege_ygsn,
       ygsn_info.growth_score_budget_ege_ygsn, ygsn_info.growth_score_paid_ege_ygsn,
       ygsn_info.numbers_paid_students_ygsn, ygsn_info.numbers_budget_students_ygsn,
       ygsn_info.numbers_student_without_exam_ygsn, ygsn_info.cost_education_ygsn
from glue_universities
    join ygsn_info on glue_universities.university = ygsn_info.university and glue_universities.data = ygsn_info.data;





