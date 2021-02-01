## 01/02/2021 v1.0.2 - Released

This bugfix release includes fix for inconsistent results when exporting the same dataset with default mapping profile.

* [MDEXP-369](https://issues.folio.org/browse/MDEXP-369) -  Inconsistent results when exporting the same dataset with default mapping profile

[Full Changelog](https://github.com/folio-org/generate-marc-utils/compare/v1.0.1...v1.0.2)
 
 ## 06/11/2020 v1.0.1 - Released
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
