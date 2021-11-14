with
    mirea as (
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
    )

select * from mirea join hse on mirea.generalname = hse.generalname and mirea.yearofdata = hse.yearofdata
where mirea.generalname!= '' and hse.generalname != '';