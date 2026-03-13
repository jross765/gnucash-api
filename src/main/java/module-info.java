module gnucash.api {

	requires java.desktop;
	requires jakarta.xml.bind;
	requires java.xml;

	requires static org.slf4j;
	
	// ----------------------------
	
	// The following already marked as "transitive" in "schnorxoborx.schnorxolib":
	requires transitive commons.configuration;
	requires transitive org.apache.commons.numbers.core;
	requires transitive org.apache.commons.numbers.fraction;
	requires transitive me.tongfei.progressbar;
	
	// Already drawn from "gnucash.base":
	// requires transitive schnorxoborx.schnorxolib;
	
	requires transitive gnucash.base;
	
	// ----------------------------
	
	exports org.gnucash.api.pricedb;
	
	exports org.gnucash.api.read;
	exports org.gnucash.api.read.aux;
	// exports org.gnucash.api.read.hlp;
	exports org.gnucash.api.read.spec;
	// exports org.gnucash.api.read.spec.hlp;
	exports org.gnucash.api.read.impl;
	exports org.gnucash.api.read.impl.aux;
	// exports org.gnucash.api.read.impl.hlp;
	exports org.gnucash.api.read.impl.spec;
	
	exports org.gnucash.api.write;
	exports org.gnucash.api.write.aux;
	// exports org.gnucash.api.write.hlp;
	exports org.gnucash.api.write.spec;
	exports org.gnucash.api.write.impl;
	exports org.gnucash.api.write.impl.aux;
	// exports org.gnucash.api.write.impl.hlp;
	exports org.gnucash.api.write.impl.spec;
}
