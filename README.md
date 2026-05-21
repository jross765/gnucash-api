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

Cf. document "[Major Changes](https://github.com/jross765/JGnuCashLibNTools/gnucash-api/major_changes.md)".

## Planned
It should go without saying, but the following items are of course subject to change and by no means a promise that they will actually be implemented soon:

* Invoices and bills: Support more variants, such as choosing the terms of payment or the "tax included" flag for entries.

* Get rid of ugly code redundancies here and there, esp. in the class `Gnucash(Writable)GenerInvoiceImpl`.

* Last not least: Provide user documentation.

## Known Issues
* Performance: When using the `Writable`-classes (i.e., generating new objects or changing existing ones), the performance is less-than-overwhelming, especially when working with larger files.

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
