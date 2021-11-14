--create database scrape

--FOREIGN KEY (forum) REFERENCES forums (slug)

CREATE TABLE IF NOT EXISTS university (
    id SERIAL PRIMARY KEY,
    name VARCHAR,
    yearOfData INT,
    averageScoreBudgetEGE NUMERIC,
    averageScorePaidEGE NUMERIC,
    growthDeclineAverageScoreBudgetEGE NUMERIC,
    growthDeclineAverageScorePaidEGE NUMERIC,
    numbersPaidStudents INT,
    numbersBudgetStudents INT,
    numbersStudentWithoutExam INT,
    averageScoreEGEWithoutIndividualAchievements BOOL,
    researchActivities NUMERIC,
    internationalActivity NUMERIC,
    financialAndEconomicActivities NUMERIC,
    salaryPPP NUMERIC,
    additionalIndicator NUMERIC,
    datasource varchar
);

CREATE TABLE IF NOT EXISTS nameUniversitiesHSE (
    id SERIAL PRIMARY KEY,
    name VARCHAR,
    generalname varchar
);

CREATE TABLE IF NOT EXISTS nameUniversitiesMIREA (
    id SERIAL PRIMARY KEY,
    name VARCHAR,
    generalname varchar
);

CREATE TABLE IF NOT EXISTS YGSN (
    id SERIAL PRIMARY KEY,
    name VARCHAR UNIQUE
);

CREATE TABLE IF NOT EXISTS universityYGSN (
    id SERIAL PRIMARY KEY,
    yearOfData INT,
    universityName VARCHAR,
    ygsnName VARCHAR,
    averageScoreBudgetEGE NUMERIC,
    averageScorePaidEGE NUMERIC,
    growthDeclineAverageScoreBudgetEGE NUMERIC DEFAULT NULL,
    growthDeclineAverageScorePaidEGE NUMERIC DEFAULT NULL,
    numbersPaidStudents INT,
    numbersBudgetStudents INT,
    numbersStudentWithoutExam INT,
    averageScoreEGEWithoutIndividualAchievements BOOL,
    costEducation NUMERIC DEFAULT NULL
);



