select student.id,
       student.region,
       student.change_region,
       json_build_array(array_agg(DISTINCT (ege.ege_id, ege.score_ege))) as json_ege,
       array_agg(DISTINCT ygsn.ygsn_id) as array_ygsn
from student
         join student_ege ege on student.id = ege.student_id
         join student_ygsn ygsn on student.id = ygsn.student_id
group by student.id limit 3
