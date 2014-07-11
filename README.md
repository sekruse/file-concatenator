file-concenator
===============

This tool can be used to merge multiple files into a single one. This is useful for merging files parallely written into a HDFS.
Consider the following structure:
```
my-output <dir>
\
 | 1 <file>
 | 2 <file>
 | ...
 | 24 <file>
```
The file-concenator replaces this directory structure with a file ```my-output``` that contains the contents of the single original files in numerically ordered by the file names.
