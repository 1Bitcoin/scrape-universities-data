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
    yearOfData
    INT,
    averageScoreBudgetEGE
    NUMERIC,
    averageScorePaidEGE
    NUMERIC,
    growthDeclineAverageScoreBudgetEGE
    NUMERIC,
    growthDeclineAverageScorePaidEGE
    NUMERIC,
    numbersPaidStudents
    INT,
    numbersBudgetStudents
    INT,
    numbersStudentWithoutExam
    INT,
    averageScoreEGEWithoutIndividualAchievements
    BOOL,
    researchActivities
    NUMERIC,
    internationalActivity
    NUMERIC,
    financialAndEconomicActivities
    NUMERIC,
    salaryPPP
    NUMERIC,
    additionalIndicator
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

CREATE TABLE IF NOT EXISTS YGSN
(
    id
    SERIAL
    PRIMARY
    KEY,
    name
    VARCHAR
    UNIQUE
);

CREATE TABLE IF NOT EXISTS universityYGSN
(
    id
    SERIAL
    PRIMARY
    KEY,
    yearOfData
    INT,
    universityName
    VARCHAR,
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
    growthDeclineAverageScoreBudgetEGE
    NUMERIC
    DEFAULT
    NULL,
    growthDeclineAverageScorePaidEGE
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
    NULL,
    numbersStudentWithoutExam
    INT
    DEFAULT
    NULL,
    averageScoreEGEWithoutIndividualAchievements
    BOOL,
    costEducation
    NUMERIC
    DEFAULT
    NULL
);

CREATE TABLE IF NOT EXISTS statistics_universities_ygsn
(
    university_name VARCHAR,
    ygsn_name VARCHAR,
    data INT,
    research_activities NUMERIC,
    international_activity NUMERIC,
    financial_and_economic_activities NUMERIC,
    salary_PPP NUMERIC,
    additional_indicator NUMERIC,

    average_score_budget_EGE
        NUMERIC
        DEFAULT
        NULL,

    average_score_paid_EGE
        NUMERIC
        DEFAULT
        NULL,

    growth_score_budget_EGE
        NUMERIC
        DEFAULT
        NULL,

    growth_score_paid_EGE
        NUMERIC
        DEFAULT
        NULL,

    numbers_paid_students
        INT
        DEFAULT
        NULL,

    numbers_budget_students
        INT
        DEFAULT
        NULL,

    numbers_student_without_exam
        INT
        DEFAULT
        NULL,

    average_score_budget_EGE_ygsn
        NUMERIC
        DEFAULT
            NULL,

    average_score_paid_EGE_ygsn
        NUMERIC
        DEFAULT
            NULL,

    growth_score_budget_EGE_ygsn
        NUMERIC
        DEFAULT
            NULL,

    growth_score_paid_EGE_ygsn
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

    numbers_student_without_exam_ygsn
        INT
        DEFAULT
            NULL,


    cost_ducation_ygsn
        NUMERIC
        DEFAULT
            NULL
);



