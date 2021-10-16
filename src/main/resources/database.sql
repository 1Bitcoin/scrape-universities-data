--create database scrape

--FOREIGN KEY (forum) REFERENCES forums (slug)

CREATE TABLE IF NOT EXISTS university (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    yearOfData INT UNIQUE,
    averageScoreBudgetEGE NUMERIC(2),
    averageScorePaidEGE NUMERIC(2),
    growthDeclineAverageScoreBudgetEGE NUMERIC(2),
    growthDeclineAverageScorePaidEGE NUMERIC(2),
    numbersPaidStudents INT,
    numbersBudgetStudents INT,
    numbersStudentWithoutExam INT,
    averageScoreEGEWithoutIndividualAchievements BOOL,
    researchActivities NUMERIC(2),
    internationalActivity NUMERIC(2),
    financialAndEconomicActivities NUMERIC(2),
    salaryPPP NUMERIC(2),
    additionalIndicator NUMERIC(2)
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
    averageScoreBudgetEGE NUMERIC(2),
    averageScorePaidEGE NUMERIC(2),
    growthDeclineAverageScoreBudgetEGE NUMERIC(2),
    growthDeclineAverageScorePaidEGE NUMERIC(2),
    numbersPaidStudents INT,
    numbersBudgetStudents INT,
    numbersStudentWithoutExam INT,
    averageScoreEGEWithoutIndividualAchievements BOOL,
    FOREIGN KEY (ysgnName) REFERENCES YGSN (name)
);



