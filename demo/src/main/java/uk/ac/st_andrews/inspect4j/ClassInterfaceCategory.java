package uk.ac.st_andrews.inspect4j;

/**
 *  The classification/type of a class or interface as ENUMs. A class can be a standard class (top-level), a local class, inner class or static nested class
 * An interface is a standard interface or nested interface - hence the NESTED category used since interfaces cannot be static.
 */
public enum ClassInterfaceCategory {
   STANDARD,
   LOCAL,
   INNER,
   STATIC_NESTED,
   NESTED
}
