Bank And Building Society Interest Frontend
=============

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html) [![Build Status](https://travis-ci.org/hmrc/bank-and-building-society-interest-frontend.svg)](https://travis-ci.org/hmrc/bank-and-building-society-interest-frontend) [ ![Download](https://api.bintray.com/packages/hmrc/releases/bank-and-building-society-interest-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/bank-and-building-society-interest-frontend/_latestVersion)

This service allows users to check their estimated taxable Bank and Building Society Interest, with the ability to update if incorrect.

Summary
-----------

Use this service to:
* View all Bank and Building Society account details
* View estimate details for interest paid to be available for current year
* View actual interest paid to be available for previous year
* Update information relating to the amount of interest paid 
* Navigate to other services such as Tax Estimate Service, where further changes can be made.

Requirements
------------

This service is written in [Scala 2.11](http://www.scala-lang.org/) and [Play 2.5](http://playframework.com/), so needs at least a [JRE 1.8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) to run.


Authentication
------------

This customer logs into this service using [GOV.UK Verify](https://www.gov.uk/government/publications/introducing-govuk-verify/introducing-govuk-verify).


Acronyms
--------

In the context of this service we use the following acronyms:

* [API]: Application Programming Interface

* [HoD]: Head of Duty

* [JRE]: Java Runtime Environment

* [JSON]: JavaScript Object Notation

* [URL]: Uniform Resource Locater

License
-------

This code is open source software licensed under the [Apache 2.0 License].

[NPS]: http://www.publications.parliament.uk/pa/cm201012/cmselect/cmtreasy/731/73107.htm
[HoD]: http://webarchive.nationalarchives.gov.uk/+/http://www.hmrc.gov.uk/manuals/sam/samglossary/samgloss249.htm
[NINO]: http://www.hmrc.gov.uk/manuals/nimmanual/nim39110.htm
[National Insurance]: https://www.gov.uk/national-insurance/overview
[JRE]: http://www.oracle.com/technetwork/java/javase/overview/index.html
[API]: https://en.wikipedia.org/wiki/Application_programming_interface
[URL]: https://en.wikipedia.org/wiki/Uniform_Resource_Locator
[State Pension]: https://www.gov.uk/new-state-pension/overview
[SP]: https://www.gov.uk/new-state-pension/overview
[JSON]: http://json.org/

[Apache 2.0 License]: http://www.apache.org/licenses/LICENSE-2.0.html
