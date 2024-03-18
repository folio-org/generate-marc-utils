## 1.9.0 - Unreleased

## 1.8.0 - Released
This release contains Java upgrade 11 -> 17 and bug fixes

[Full Changelog](https://github.com/folio-org/generate-marc-utils/compare/v1.7.0...v1.8.0)

### Bug fixes
* [GMU-42](https://folio-org.atlassian.net/browse/GMU-42) - Switch Java and JDK from 11 to 17
* [GMU-41](https://folio-org.atlassian.net/browse/GMU-41) - json-path 2.9.0, guava 33.0.0-jre fixing vulns
* [MDEXP-652](https://folio-org.atlassian.net/browse/MDEXP-652) - "Linking ISSN" (022$l) and "Invalid ISBN" (020$z) missed for  MARC bib records generated on the fly

## 1.7.0 - Released
This release contains only changing version of json-path.

[Full Changelog](https://github.com/folio-org/generate-marc-utils/compare/v1.6.0...v1.7.0)

### Bug fixes
* [GMU-38](https://issues.folio.org/browse/GMU-38) - json-path 2.8.0

## 1.6.0 - Released
This release includes logging improvements configuration and dependency updates

[Full Changelog](https://github.com/folio-org/generate-marc-utils/compare/v1.5.0...v1.6.0)

### Bug fixes
* [GMU-36](https://issues.folio.org/browse/GMU-36) - Jackson 2.14.0, log4j 2.19.0, commons-io 2.11.0, json-path 2.7.0

### Technical tasks
* [GMU-35](https://issues.folio.org/browse/GMU-35) - Logging improvement - Configuration

## 1.5.0 - Released
This release was primarily focused on bug fixes

[Full Changelog](https://github.com/folio-org/generate-marc-utils/compare/v1.4.0...v1.5.0)

### Bug fixes
* [GMU-34](https://issues.folio.org/browse/GMU-34) - Holdings notes not being exported properly in MARC Bibs when there are multiple holdings
* [GMU-33](https://issues.folio.org/browse/GMU-33) - Holdings statement mapped incorrectly in custom mapping profile

## 1.4.0 - Released
The primary focus of this release was to fix bugs and add alternative titles logic

[Full Changelog](https://github.com/folio-org/generate-marc-utils/compare/v1.3.0...v1.4.0)

### Technical tasks
* [GMU-10](https://issues.folio.org/browse/GMU-10) -  Log4j vulnerability verification and correction

### Stories
* [GMU-25](https://issues.folio.org/browse/GMU-25) - Add alternative titles type usage

### Bug fixes
* [GMU-28](https://issues.folio.org/browse/GMU-28) - Error log provides duplicate entries for the same error
* [GMU-24](https://issues.folio.org/browse/GMU-24) - Missing data in one subfield excludes whole field from export with the custom mapping profile
* [MDEXP-480](https://issues.folio.org/browse/MDEXP-480) - Only one mapping is executed during the export

## 1.3.0 - Released
This release includes modifying logic of processing for composite Rules.

[Full Changelog](https://github.com/folio-org/generate-marc-utils/compare/v1.1.1...v1.3.0)

### Stories
* [GMU-7](https://issues.folio.org/browse/GMU-7) - Missing holdings statement causes permanent location to be omitted in the export

## 04/02/2021 v1.1.1 - Released
This bugfix release includes fix for inconsistent results when holdings and items data incorrectly appended to the srs record
in MARC file.

[Full Changelog](https://github.com/folio-org/generate-marc-utils/compare/v1.1.0...v1.1.1)

### Bug Fixes
* [MDEXP-385](https://issues.folio.org/browse/MDEXP-385) - Holdings and items data incorrectly appended to the srs record

## 03/10/2021 v1.1.0 - Released
 Major version release which includes below features :
 * Improve error handling to detect which record exactly leads to the exception during the export to the error logs
 * Add new translation functions for Related Identifiers and Holdings Permanent Location fields
 * Remove RMB and Vert.x dependencies
 * Add personal data disclosure form

 [Full Changelog](https://github.com/folio-org/generate-marc-utils/compare/v1.0.2...v1.1.0)

### Stories
* [GMU-1](https://issues.folio.org/browse/GMU-1) - Detect which record exactly leads to the exception during the export to the error logs
* [GMU-2](https://issues.folio.org/browse/GMU-2) - Remove RMB dependency
* [MDEXP-12](https://issues.folio.org/browse/MDEXP-12) - Create translation function for holding permanent location
* [MDEXP-191](https://issues.folio.org/browse/MDEXP-12) - Create translation function for related identifiers
* [MDEXP-358](https://issues.folio.org/browse/MDEXP-358) - Add personal data disclosure form

### Bug Fixes
* [MDEXP-318](https://issues.folio.org/browse/MDEXP-318) - Update error codes
* [MDEXP-351](https://issues.folio.org/browse/MDEXP-351) - Fix empty record present in marc file
* [GMU-3](https://issues.folio.org/browse/GMU-3) - Fix class cast exception during fields mapping
* [MDEXP-345](https://issues.folio.org/browse/MDEXP-345) - Subfield $3 not always present when multiple holdings and items are associated with the instance
* [MDEXP-367](https://issues.folio.org/browse/MDEXP-367) - Provide granular error in the error log so that the same information is not repeated multiple times

## 01/02/2021 v1.0.2 - Released
 This bugfix release includes fix for inconsistent results when exporting the same dataset with default mapping profile.

 * [MDEXP-369](https://issues.folio.org/browse/MDEXP-369) -  Inconsistent results when exporting the same dataset with default mapping profile

 [Full Changelog](https://github.com/folio-org/generate-marc-utils/compare/v1.0.1...v1.0.2)

## 11/06/2020 v1.0.1 - Released
 This bugfix release includes fixes for the mapping process to fail due to a change in date formats in the metadata,
 it also includes  changes to support subfield 3 that contain holding hrid for record with item type in MARC file.
 A part from that, it contains a fix of missing standard number and GPO item identified if identifier type in inventory-storage
 is present in uppercase or lowercase format and removes the unused guava library.

[Full Changelog](https://github.com/folio-org/generate-marc-utils/compare/v1.0.0...v1.0.1)

### Bug Fixes
* [MDEXP-307](https://issues.folio.org/browse/MDEXP-307) - Fix security dependency issue
* [MDEXP-308](https://issues.folio.org/browse/MDEXP-308) - Subfield $3 is missing for the MARC tags with item data
* [MDEXP-326](https://issues.folio.org/browse/MDEXP-326) - Missing Standard Number and GPO Item identifiers

## 10/14/2020 v1.0.0 - Released

 * Initial module release with common functionalities for generating records in MARC format
 * Has capabilities to read records in json format, parse and apply translation functions
