--create database scrape

--FOREIGN KEY (forum) REFERENCES forums (slug)

CREATE TABLE IF NOT EXISTS university (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
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
    additionalIndicator NUMERIC
);

CREATE TABLE IF NOT EXISTS YGSN (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS universityYGSN (
    id SERIAL PRIMARY KEY,
    yearOfData INT,
    universityName VARCHAR(255),
    ysgnName VARCHAR(255),
    averageScoreBudgetEGE NUMERIC,
    averageScorePaidEGE NUMERIC,
    growthDeclineAverageScoreBudgetEGE NUMERIC,
    growthDeclineAverageScorePaidEGE NUMERIC,
    numbersPaidStudents INT,
    numbersBudgetStudents INT,
    numbersStudentWithoutExam INT,
    averageScoreEGEWithoutIndividualAchievements BOOL,
    costEducation NUMERIC DEFAULT NULL,
    FOREIGN KEY (ysgnName) REFERENCES YGSN (name)
);



