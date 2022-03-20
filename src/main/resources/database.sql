--create database scrape

--FOREIGN KEY (forum) REFERENCES forums (slug)

CREATE TABLE IF NOT EXISTS university
(
    id
    SERIAL
    PRIMARY
    KEY,
    name
    VARCHAR,
    region
    VARCHAR,
    yearOfData
    INT,
    hostel
    BOOL,
    averageAllStudentsEGE
    NUMERIC,
    dolyaOfflineEducation
    NUMERIC,
    averagedMinimalEGE
    NUMERIC,
    averageBudgetEGE
    NUMERIC,
    countVserosBVI
    INT,
    countOlimpBVI
    INT,
    countCelevoiPriem
    INT,
    dolyaCelevoiPriem
    NUMERIC,
    ydelniyVesInostrancyWithoutSNG
    NUMERIC,
    ydelniyVesInostrancySNG
    NUMERIC,
    averageBudgetWithoutSpecialRightsEGE
    NUMERIC,
    datasource
    varchar
);

CREATE TABLE IF NOT EXISTS nameUniversitiesHSE
(
    id
    SERIAL
    PRIMARY
    KEY,
    name
    VARCHAR,
    generalname
    varchar
);

CREATE TABLE IF NOT EXISTS nameUniversitiesMIREA
(
    id
    SERIAL
    PRIMARY
    KEY,
    name
    VARCHAR,
    generalname
    varchar
);

CREATE TABLE IF NOT EXISTS universityYGSN
(
    id
    SERIAL
    PRIMARY
    KEY,
    universityName
    VARCHAR,
    yearOfData
    INT,
    ygsnName
    VARCHAR,
    averageScoreBudgetEGE
    NUMERIC
    DEFAULT
    NULL,
    averageScorePaidEGE
    NUMERIC
    DEFAULT
    NULL,
    numbersPaidStudents
        INT
        DEFAULT
        NULL,
    numbersBudgetStudents
        INT
        DEFAULT
        NULL
--     growthDeclineAverageScoreBudgetEGE
--     NUMERIC
--     DEFAULT
--     NULL,
--     growthDeclineAverageScorePaidEGE
--     NUMERIC
--     DEFAULT
--     NULL,
--     numbersStudentWithoutExam
--     INT
--     DEFAULT
--     NULL,
--     averageScoreEGEWithoutIndividualAchievements
--     BOOL,
--     costEducation
--     NUMERIC
--     DEFAULT
--     NULL
);

CREATE TABLE IF NOT EXISTS statistics_universities_ygsn
(
    university VARCHAR,
    ygsn_name VARCHAR,
    data INT,
    region VARCHAR,
    hostel BOOL,

    average_all_students_ege
        NUMERIC,

    dolya_offline_education
        NUMERIC
        DEFAULT
        NULL,

    averaged_minimal_ege
        NUMERIC
        DEFAULT
        NULL,

    average_budget_ege
        NUMERIC
        DEFAULT
        NULL,

    count_vseros_bvi
        INT
        DEFAULT
        NULL,

    count_olimp_bvi
        INT
        DEFAULT
        NULL,

    count_celevoi_priem
        INT
        DEFAULT
        NULL,

    dolya_celevoi_priem
        NUMERIC
        DEFAULT
        NULL,

    ydelniy_ves_inostrancy_without_sng
        NUMERIC
        DEFAULT
            NULL,

    ydelniy_ves_inostrancy_sng
        NUMERIC
        DEFAULT
            NULL,

    average_budget_without_special_rights_ege
        NUMERIC
        DEFAULT
            NULL,

    average_score_budget_ege_ygsn
        NUMERIC
        DEFAULT
            NULL,

    average_score_paid_ege_ygsn
        NUMERIC
        DEFAULT
            NULL,

    numbers_paid_students_ygsn
        INT
        DEFAULT
            NULL,

    numbers_budget_students_ygsn
        INT
        DEFAULT
            NULL,


    json_ygsn
        varchar
        DEFAULT
            NULL
);

CREATE TABLE distribution_students (
     id SERIAL,
     region VARCHAR(100),
     count_vyp INT,
     count_participant INT,
     count_100ball INT,
     PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS student (
       id SERIAL PRIMARY KEY,
       region VARCHAR(100),
       change_region bool
);

CREATE TABLE IF NOT EXISTS ege (
     id SERIAL PRIMARY KEY,
     name varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS ygsn
(
    id SERIAL PRIMARY KEY,
    name VARCHAR UNIQUE
);

CREATE TABLE IF NOT EXISTS student_ege(
     id SERIAL PRIMARY KEY,
     student_id INTEGER,
     ege_id INTEGER,
     score_ege NUMERIC,
     FOREIGN KEY (ege_id) REFERENCES ege (Id),
     FOREIGN KEY (student_id) REFERENCES student (Id)
);

CREATE TABLE IF NOT EXISTS university_ygsn_mirea(
      id SERIAL PRIMARY KEY,
      university_id INTEGER,
      ygsn_id INTEGER,
      contingentStudents NUMERIC,
      dolyaContingenta NUMERIC,
      numbersBudgetStudents INTEGER,
      averageScoreBudgetEGE NUMERIC,
      FOREIGN KEY (ygsn_id) REFERENCES ygsn (Id),
      FOREIGN KEY (university_id) REFERENCES university (Id)
);

CREATE TABLE IF NOT EXISTS ygsn_ege(
    id SERIAL PRIMARY KEY,
    ege_id INTEGER,
    ygsn_id INTEGER,

    FOREIGN KEY (ygsn_id) REFERENCES ygsn (Id),
    FOREIGN KEY (ege_id) REFERENCES ege (Id)
);








