# Snox
The SNOX Programming Language: A first approach to create my own little programming language written in Kotlin

### Overview
Snox is a dynamically (at runtime) interpreted programming language.
It does not do any static code analysis (no compiling in is purest sense),
but it does some syntax error checking while parsing.

### Current State
As for now, Snox can parse and execute simple assignements like expressions,
it understand variable declaration and their scoping separated by blocks.
However, there´s currently no support for classes and functions (which will come soon).

### Basic Syntax

Variable Declaration: `var myVar = myInit` (No Init needed; default is `nil`)
Expressions: `print myVarA + myVarB`

### Types
Currently supported: Numbers(based on Kotlins Double) and Strings