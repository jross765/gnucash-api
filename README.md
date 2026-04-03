# Notes on the Module "API"

## What Does It Do?

This is the core module of the project, providing all low-level read-/write access functions to a 
GnuCash 
file.

## What is This Repo's Relationship with the Other Repos?

* This is a module-level repository which is part of a multi-module project, i.e. it has a parent and several siblings. 

  [Parent](https://github.com/jross765/JGnuCashLibNTools.git)

* Under normal circumstances, you cannot compile it on its own (at least not without further preparation), but instead, you should clone it together with the other repos and use the parent repo's build-script.

* This repository contains no history before V. 1.7 (cf. notes in parent repo).

## Major Changes 
### V. 1.7.1 &rarr; 1.8

There have been some major changes in this release:

* Introduced important add-on: *exact* numbers / computations, as opposed to current implementation 
  based on class `FixedPointNumber` in `SchnorxoLib`, which allows for high/arbitrary-precision 
  computations, but not for exact ones).
    
  (Based on `SchnorxoLib` V. 0.2 and
  class `BigFraction` in Apache Commons Numbers. &rarr; new dependency)

  That applies to *all* classes returning or receiving values -- they
  now basically are all available in two variants throughout the lib.
    
   * Package "`read`":
     * `GnuCashTrxSplit(Impl)`: New methods `getValueRat()` and `getQuantityRat()`

       ("rat" for "rational number") that return the according values as
       fractions / rational numbers.

     * `GnuCashAccount(Impl)`, `GnuCashTransaction(Impl)`: new methods `getBalanceXYZRat()`

       (symmetric to already existing ones) that return balance of account / transaction as *exactly computed* number (i.e., as fraction).

      * Etc., many instances throughout the lib.

    * Package "`write`": accordingly.
    * Package "`currency`":
       * Renamed to "`pricedb`" (old name was misleading).
       * Introduced `BigFraction` variants. 

         As `FixedPointNumber` is mutable and the old implementation 
         of this package's methods were based on this fact, the 
         interfaces and implementation had to be changed 
         (`BigFraction` is immutable).

       * Other major changes in implementation (incl. logic); went from 
         "one table per name space" to "one table per commodity type",
         as well as better (though not perfect) type safety.
       * Small improvements

* `GnuCashAccount(Impl)`: Method `isHidden()`.

* `GnuCash(Writable)Transaction(Impl)`: Moved parts of code to new module "API Specialized Entities".

* Loading files now shows progress bars for the parts that typically take longer: Price-DB and transactions (optional, off by default).

* Not-so-small changes in interfaces and extensive refactoring under the hood 
  (primarily induced by introduction of "`BigFraction`" variants of methods, cf. above).

* Various small improvements here and there.

### V. 1.7.0 &rarr; 1.7.1
* Small corrections / bug fixes:
  * `GnuCashGenerInvoiceImpl`: Fixed bug in `getXYZDateFormatted()`.
  * `GnuCashTransactionImpl`: 
    * Dto. (as for invoices)
    * Added `getDateEnteredFormatted()` (consistency, had been forgotten).

* Little bit of cleanup-work.

### V. 1.6 &rarr; 1.7
* Introduced new (dummy) ID types (cf. module "Base") for type safety and better symmetry with sister project (inherited from module "Base" and also used in all downstream modules accordingly in order to fully leverage the measure).

  Cf. module "Base"'s README file for the rationale.

  The really imporant part, as mentioned there, was changing the interfaces:

  `getAccountByID(GCshID acctID)` &rarr; `getAccountByID(GCshAcctID acctID)`

  It is worth noting that by this measure alone, the author has been
  able to spot and fix a non-trivial and subtle bug.

  I decided *not* to keep the old methods as deprecated variants in the interfaces for a while, for it seems that not many people are actually using this lib apart from the current author at this point.

* `GnuCashAccount(Impl)`: 
  * New method `getReconcileInfo()`
  * New mehood `printTree()`
  * New method `getChildrenRecursive()` (symmetry with sister project)

* Got rid of ugly `Const_XY` classes, replaced them by `Const_Transl` and properties files instead.

* `GnuCashFile(Impl)`: 
  * Changed return type of *all* `getAccountsXYZ()` from Collection to List.
  * Dto. for `getTransactionsXYZ()`.
  * Dto. for `getTransactionSplitsXYZ()`.
  * Dto. for `getCommoditiesXYZ()`.
  * Dto. for `getPricesXYZ()`.
  * New method `dump()`.

* `GnuCash(Writable)FileImpl`: Implementation now honors the usual convention for guessing the file read/write option from the file name: "xml" &rarr; non-compressed; "gnucash" &rarr; compressed.

  [ This makes sense and improves the symmetry with the sister project, although the author aknowledges that the original GnuCash software does not like to open files with other extensions than "gnucash". However, 
  he has some more plans concerning (non-)compressed test data files
  in the pipeline. ]

* `GCsh(Writable)Address(Impl)`: Changed method names (kept old ones as deprecated variants for compatibility).

* Improved test coverage

### V. 1.5 &rarr; 1.6
* Fixed a couple of bugs in write-branch of API (for various entities), esp. in object-deleting code.

* Introduced `GCsh(Writable)AccountLot(Impl)`, which has a similar relationship to `GnuCashAccount` as `GnuCash(Writable)TransactionSplit(Impl)` has to `GnuCashTransaction`.
    
  This is needed to report on lots in securities (stock) accounts,
  and that, in turn, is needed to prepare German tax filings (so-called "FIFO principle").

* `GnuCashWritableTransactionSplitImpl`: Fixed bug in `setQuantity()`.

* `GnuCashWritableAccount(Impl)`, `GnuCash(Writable)File(Impl)`: Expanded interface and implemented it.
    
* `GnuCash(Writable)TransactionSplit(Impl)`: 
   Removed "formatted for HTML" methods -- don't see a real need for it, and even if there was one, then it would belong elsewhere (still thinking of removing the "formatted (without HTML)" methods as well; I see them in the grey area).

* Significantly improved overall test coverage, esp. in write-branch.

* Various minor changes, cleaning and improving code.

### V. 1.4 &rarr; 1.5
* `GnuCash(Writable)Commodity(Impl)`: New methods `get(Writable)StockAccounts()`, which is a handy short-cut for specific use cases.

* `GnuCash(Writable)File(Impl)`: Implemented empty skeleton methods that had been forgotten and previously just returned null, such as `getAccountsByName()`.

* Added a few method implementations that had been forgotten here and there.

* Re-iterated over some code, e.g.:
	* Handling of invoice entries (all variants)
	* `GnuCashJobInvoice(Entry)` vs `GnuCashGenerInvoice(Entry)` (inconsistencies)
	* `GnuCash(Writable)GenerInvoice(Impl)` vs. `GnuCash(Writable)GenerJob(Impl)`
	* `GnuCash(Writable)[Customer|etc](Impl)`
	* `GnuCashCommodityImpl`
	* `GnuCash(Writable)FileImpl`
	* `GCshOwner(Impl)`
	* Improved overall robustness by more consistently checking method parameters.

* Finally completed list of internal manager classes `File[XYZ]Manager`.

* Minor changes in interfaces.

* Fixed a couple of bugs.

* Some code-cleanup here and there.

* Improved test coverage.

### V. 1.3 &rarr; 1.4
* Extracted some basic packages to new module "Base".

* Non-trivial clean-up work, most of it under the hood. Among other things: 
  * Changed the semantics of `GnuCashObject(Impl)` (taken from sister project): Handling of slots now has moved to `Has(Writable)UserDefinedAttributes(Impl)`. Simulaneously, got rid of some code redundancies.
  * Introduced class `GnuCashGenerJobImpl` and changed implementation of 
its derivatives `GnuCash[Customer|Vendor]JobImpl` so that it is now symmetrical to the implementation of `GnuCashGenerInvoiceImpl` and its derivatives `GnuCashCustomerInvoiceImpl` etc.
  * Changed interfaces: Used `List` instead of `Collection` where appropriate (not everywhere).

* Bug-fixing.

* Changed all interface and class names from `GnucashXYZ` to `GnuCashXYZ`, following the usual spelling convention.

* De-cluttered interfaces.

* Improved test coverage.

### V. 1.2 &rarr; 1.3
* Introduced interfaces/classes for employees, emloyee vouchers and employee jobs, completely analogous to the according structures for customers and vendors in V. 1.1 (except for the fact that there are no "employee jobs" that one might expect):
  * `Gnucash(Writable)Employee(Impl)` 
  * `Gnucash(Writable)EmployeeVoucher(Impl)` 

* Generalized (technically) locale-specific code (e.g., GnuCash stores certain semi-internal XML-tags with locale-specific values for transaction splits' actions). Until V. 1.2, all this was tightly tied to the german locale with hard-coded valus (de_DE). Now, we have a generalized approach open for all locales (still hard-coded, though), and support for the following locales is actually implemented:
  * English
  * French
  * Spanish
  * German

* Enhanced type safety by: 
  * introducing package `org.gnucash.basetypes`, containing, among other things, the class `GCshID` as a wrapper for the UUIDs used throughout the system (instead of just treating them as unsafe strings).
  * introducing several enums where before we had string constants,

* Partially re-wrote package `org.gnucash.currency`: Safer and clearer implementation, now leveraging newly introduced types from package `org.gnucash.basetypes`.

* Re-iterated and completed code in sub-package "`aux`" (both in read- and in write-branch): Now, classes and interfaces are cleanly separated for read- and write-operations *comme il faut*.

* Renamed `GCsh(Writable)Price(Impl)` to `Gnucash(Writable)Price(Impl)` and moved it from the "`aux`" sub-package up one level, as, in fact, price is a main entity.

* Introduced new packages "`hlp`" (both for interfaces and implementations) for interfaces/classes that are purely internal. *Note: This is not the same as the "`aux`" packages, the contents of which are auxiliary but public).*

* Significantly improved test coverage. In so doing, found and fixed a couple of bugs.

* Lots of minor improvements and code-cleaning. Among the major changes here: 

  * De-cluttered classes `Gnucash(Writable)FileImpl` by introducing helper classes in sub-package "`hlp`". All this greatly improved the readability and maintainability of the code.
  * Updated the JavaDoc-descriptions (still a lot of work to do, though...).

* Last not least: From this version on, the author tries to keep the project as symmetrical as possible with its sister project, `JKMyMoneyLib`.

### V. 1.1 &rarr; 1.2
* Introduced interfaces/classes `Gnucash(Writable)Commodity(Impl)` for reading and writing "commodities", in GnuCash lingo an umbrella term for 
  * Currencies
  * (Real) Securities (shares, bonds, funds, etc.)
  * Possibly other assets that can be mapped to this GnuCash entity as pseudo-securities, such as crypto-currencies, physical precious metals, etc.

* Reading of commodities' prices is possible, but writing them is not possible yet. Given the above polymorphism of the commodity (cf. above), a "price" is one of the following:

  * A currency's exchange rate
  * A security's quote
  * A pseudo-security's price

   (all of them usually, but not necessarily, noted in the default currency)

  Given that we have several variants of `CmdtyCurrID` (cf. below), we also have several methods returning various types.

In this context, the following was also necessary/also made sense:

* Introduced auxiliary interface/class `GCshPrice(Impl)` representing an entry of the GnuCash price database.

* Introduced auxiliary class `GCshCmdtyCurrID` (essentially a pair of commodity name space and commodity code), which represents a security-style pseudo-ID for commodities (thus, including currencies, cf. above). In addition to that, introduced (grand-)child classes `GCshCmdtyID`, `GCshCurrID`, `GCshCmdtyID_Exchange`, `GCshCmdtyID_MIC` and `GCshCmdtyID_SecIDType` to represent various variants with adequate type-safety, i.e. for both easy and safe handling of read and write operations. 

  Note that, as opposed to all other entities in the GnuCash XML file, commodities have no internal UUID (a fact that the author finds irritating). This is one of the reasons why (pseudo-)currencies necessitate a fundamentally different approach.

* Closely linked to `GCshCmdtyCurrID` and its descendants: Major rework on the class `GCshCmdtyCurrNameSpace` (formerly `CurrencyNameSpace`): introduced several enums for enhanced type safety.

  *Background*: In and of itself, GnuCash's commodity name space can be freely defined; only suggestions are made on how to define it, e.g. a major stock exchange's abbreviation or a major securities index name. But you *need* not do it like that. The author is well aware of the fact that the GnuCash developers have -- likely after having weighed the pros and cons -- purposefully decided *not* to enforce any pre-defined values. 

  In this lib, we follow this spirit (as well as the path that the original author Marcus Wolschon followed): We *allow* the users to freely define any name space they want, but we *encourage* them to use one of the pre-defined enums in `GCshCmdtyCurrNameSpace` (expecting that buying, holding and selling conventional securities on major markets will be the most typical use case by far).

  *Note that the current definition of the enums in the class `GCshCmdtyCurrNameSpace` is by no means final and "once and for all". The author rather expects some re-work iterations on the existing ones and the introduction of new ones.*

  [ Some readers might be interested in the fact that the author uses the name space `GCshCmdtyCurrNameSpace.SecIdType.ISIN`, because he believes that this is the only one that allows easy and consistent handling of a global portfolio (and the fact that he is not so much interested in where a specific security is traded, because he follows a buy-and-hold investment strategy). ]

Further improvements:

* Interface changes in:

  * `Gnucash(Writable)Account`
  * `Gnucash(Writable)Transaction`
  * `Gnucash(Writable)File`

  Essentially leveraging improved type safety and robustness, primarily through use of `GCshCmdtyCurrID` and descendants.

* Improved retrieval of objects in `GnucashFileImpl` with `getXYZByName()`-methods (e.g., `getAccountsByName()`): There are now two variants for (almost) each of them: One with exact matching and one with relaxed matching, the latter one possibly returning several objects. (Still some work to be done here (esp. with account variants.) Cf. test cases and example programs.

* Improved test coverage.

* Some minor bug-fixing here and there.

### V. 1.0 &rarr; 1.1
* Reading and writing of (technical/generic) invoices: Not just customer invoices, but also vendor bills now. According to the internal XML structure and the part of the code that is generated from it (i.e., the XSD file), both derive from a common class that represents a "generic" invoice. In addition to that, there is also a third type: job invoice (cf. next item).
    
    ==> Introduced specialized interfaces/classes: `GnucashCustomerInvoice(Impl)`, `GnucashVendorBill(Impl)` and `GnucashJobInvoice(Impl)`, which all derive from `GnucashGenerInvoice(Impl)`. Consequently, we have the same for their entries: `GnucashGenerInvoiceEntry(Impl)`, etc.

    (This didn't seem too difficult at first, but actually it was a lot of work.)

* Invoices and bills (both customer's and vendor's) can now be assigned directly to the customer/vendor or indirectly via a job (as opposed to V. 1.0, where everything was indirect via a job). In business terms, the indirect ones are still customer invoices/vendor bills, of course, but the technical XML structure mandates a sligthly different approach: If there is a job, then the invoice's/bill's owner is not the according customer/vendor, but rather the job object, which in turn is owned by a customer/vendor. Thus, analogously to the invoices, we now also have two types of jobs: customer jobs and vendor jobs.

    ==> Introduced specialized interfaces/classes: `GnucashCustomerJob(Impl)` and `GnucashVendorJob(Impl)`, which both derive from `GnucashGenerJob(Impl)`. Both interfaces and implementations.

   (Once the splitting-up of the classes for invoices was done, this part was not too difficult.)

* Introduced handling for terms based on class `GCshBillTerms`: Both the infos of the general list as well as the customers'/vendors' default terms can be read with all details now. In this version, write-operations are only possible in the form of references (i.e., assigning already-existing terms to a customer or a vendor without changing the term details themselves).

* Better handling of tax tables based on class `GCshTaxtable(Entry)`: Analogous to terms above.

* More complete coverage of data access to customer/vendor data:

  * discount/credit (customers only)
  * default tax table (had been forgotten for vendors)
  * default terms (had not been available for both)

* Introduced new packages (both for interfaces and implementations):

   *  "spec" for the above-mentioned special variants of generic classes.
   *  "aux" for auxiliary classes (and for them, a special prefix: `GCsh`). Also, made code both redundancy-free and more readable by getting rid of nested classes (e.g., `Gnucash(xyz)Invoice.Address` &rarr; `GCshAdress` or `GGshTaxTable.TaxTableEntry` &rarr; `GCshTaxTableEntry`).

* Improved exception handling/actual working of code with real-life data (not fundamentally different, but rather small repair work -- the code partially did not work in the above-mentioned environment).

* JUnit-based set of regression test cases (with test data in dedicated test GnuCash file). This alone greatly improves security and peace of mind both for users and developers, let alone learning how to actually use the library (in lack of a proper documentation, simply look into the examples and the test cases to understand how to use the lib).

* Enhanced type safety and compile-time checks -- both were not always as strict as possible.

* (Partially) got rid of overly specific and/or obsolete code (e.g., there were methods that only make sense when using the German standard SKR03/04 chart of accounts, or the 19% VAT (originally, until 2006, 16%, which you still could find in the code)).

* Got rid of some redundancies here and there, introduced class `Const` for that (which in turn contains hard-coded values).

* Got rid of (public) methods that accept internal IDs as arguments: First, internal IDs are -- well -- internal/internally generated and thus should not be part of an API, and second, GnuCash uses only UUIDs anyway, and there is simply no point in generating these outside (to be fair: for all of these methods, there were wrappers without).

* Renamed some classes to honour naming conventions (e.g., `abcMyObjectWritingxyz` &rarr; `abcWritableMyObjectxyz`).

* Some minor cleaning here and there (e.g., small inconsistencies in date-handling, `toString()`-methods, etc.).

* Provided an extensive set of example programs (not generally-usable tools!) in a module of its own. Moved the one single example program that was there before into this module.

## Planned
It should go without saying, but the following items are of course subject to change and by no means a promise that they will actually be implemented soon:

* Invoices and bills: Support more variants, such as choosing the terms of payment or the "tax included" flag for entries.

* Introduce special variant of transaction: Simple transaction (with just two splits).

* Possibly carve out a new module for all stuff in "`spec`" packages.

* Get rid of ugly code redundancies here and there, esp. in the class `Gnucash(Writable)GenerInvoiceImpl`.

* Last not least: Provide user documentation.

## Known Issues
* Performance: When using the `Writable`-classes (i.e., generating new objects or changing existing ones), the performance is less-than-overwhelming, especially when working on larger files.

* As mentioned in the parent repo's README: As of now, the lib only works well when your GnuCash files are generated on a handful of system locales.

* *Edge case*: The "virgin" test file (the practically empty one) cannot be parsed -- in the current stage of development, the lib still assumes a handful of entities being already there, such as the Price DB.

* *Edge case*: Generating new objects currently only works (reliably) when at least one object of the same type (a customer, say) is already in the file.

* When generating invoices, you cannot/should not call the method `post()` immediately after composing the object. 
  It will seemingly work (not throw an exception), but the amount of the post-transaction will be wrong (thus, the 
  transaction will be useless as it cannot be corrected manually in GnuCash; post-transactions are read-only). 

  Instead, you should first write the results to the output file using the `GnucashWritableFile.writeFile()`-method, then re-load/re-parse the invoice generated before and then use the `post()`-method. Then, the amount will be correct.

  Cf. test classes `TestGnucashWritableCustomerInvoiceImpl`, `TestGnucashWritableVendorBillImpl` and  `TestGnucashWritableJobInvoiceImpl`.

* GnuCash (the original software) "tricks" you with security symbols:
  When you enter the same string into the "symbol" field and into the "ticker" field (technically "code"),
  then GnuCash will *not* save that symbol redundantly in a separate field, as you would 
  expect and saves nothing for the symbol in the XML file (not even an indicative flag). 

  Instead, GnuCash shows you the ticker. Only when symbol and ticker are two *different* 
  strings, GnuCash saves it as an additional key-value pair for the commodity in the 
  according XML structure.

  The current maintainer is very well aware of the fact this is not an accident, but that 
  the GnuCash developers have done this on purpose. He maintains, however, that this is
  misguided. 
  This lib, consequently, does not follow GnuCash's peculiar logic here but will instead 
  "stubbornly" (and correctly!) get and set the above-mentioned key-value pair 
  -- and only it -- with the methods `GnuCashCommodity.[get|set]Symbol()`. Ticker and symbol,
  in this lib, are two different fields, period.

  Consequently, you will get irritating results:
   * when using the tool `GetSecInfo` (module gnucash-tools).

   * when using the tools `GetAcctInfo` and `GetTrxSpltInfo` (module gnucash-tools): 

     These two show show you account-balance and transaction split value and -quantity, 
     all of which, in certain cases, are not of type "currency" but of type "security" 
     (e.g.: "15 MBG" is "15 shares of Mercedes Benz Group" in the test data file). 
     But given the above difference in GnuCash and this lib, you will see "15 DE0007100000" 
     instead.

   * when using the viewer (module gnucash-viewer).

  Please do not blame the maintainer for this.

* XSD schema file (the one used to generate the "base" code of the lib) is not suitable to validate the GnuCash files read and generated.

  Sure, one would expect that the files are valid when checked against the relevant XSD file, but this is not as dramatic as it sounds:
   * For one, plenty of tests as well as real-life usage have proven that the lib works very well. 
   * Additonally, oddly enough, it seems that the GnuCash developers themselves do not put too much emphasis on this aspect. They do not even maintain an official XSD file (the one used in this project has evolved from a file originally written/generated by the first author some 15 years ago and now is being mainained manually by re-engineering the GnuCash file format). 

     They do, however, have a sort-of half-official RNC file, from which an XSD file can be generated, but 
      * the author has read somewhere in their documentation that it is not used "in earnest", but rather exists in the shadows, so to speak,
      * the generated (set of) XSD file(s) is *very* different from the one used right now, and the author hesitates to replace it at this point, and
      * apart from the previous two points: it is useless -- the original (!) GnuCash-generated files are not valid according to the XSD files generated from the half-official RNC file. 
        So what's the point?
