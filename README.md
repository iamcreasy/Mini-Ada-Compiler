# Mini-Ada-Compiler
This is a mini-Ada to x86 compiler(16 bit).
This version of Ada supports only integer and string(limited) data type, and allows assignment and IO statement. 
In an assignment statement, it supports expressions, with addition, multiplication and unary negation operator.
If all operators have same precedence in an expression, it uses right to left associativity.
In an IO statement, it supports integer input/output, and string output.

## Backend
We have to run the following command to compile our compiler,
```bash
javac adac.java
```
Now our compiler is ready to take ada source file.

Example Hello.ada file contains,
```Ada
procedure main is
begin
  putln("Hello World");
end main;
```

To compile Hello.ada we have to run the following command,
```bash
$ java adac Hello.ada
Parsing successful. Output at Hello.tac
TAC to x86 translation sucessful. Output at Hello.asm
```

Our compiler would would create Hello.tac and Hello.asm file. Hello.TAC
([Three Address Code](https://en.wikipedia.org/wiki/Three-address_code)) file contains our intermediate representation, 
and Hello.ASM contains the x86 instructions. 

Contant of the Hello.TAC file,
```
PROC    _MAIN   
wrs     _s0     
wrln    
ENDP    _MAIN   
START   PROC    _MAIN   
```
Contant of the Hello.ASM file,
```asm
		.model small
		.586
		.stack 100h
		.data
_s0    db      "Hello World","$"
		.code
		include io.asm

		  ;PROC    _MAIN   
_MAIN	proc
		  push bp
		  mov bp, sp
		  sub sp, 0

		  ;wrs     _s0     
		  mov dx, offset _s0
		  call writestr

		  ;wrln    
		  call writeln

		  ;ENDP    _MAIN   
		  add sp, 0
		  pop bp
		  ret 0
_MAIN	ENDP

		  ;START   PROC    _MAIN   
main	PROC
		  mov ax, @data
		  mov ds, ax
		  call _MAIN
		  mov ah, 4ch
		  int 21h
main	ENDP
		  END main

```
(Note: that all user defined identifiers has an underscore prefix added to them in both TAC and ASM file.)

## Frontend
Now we will use [MASM32](http://www.masm32.com) to translate our x86 instructions to object code. The files `ml.exe` and `link16.exe` are found under `<masm32>/bin`

Since [IO.asm](https://github.com/iamcreasy/Mini-Ada-Compiler/blob/master/io.asm) contains the IO macros we will need copy IO.asm in the same direcoty as Hello.ASM

The following command to translate our x86 instruction to machine code will create `Hello.obj`
```
ml /c Hello.asm
```

The following intruction to statically link the content of IO.ASM with Hello.OBJ will create final 16bit executable file `Hello.exe`
```
link16 Hello.obj;
```

## Running the executable
Since Hello.exe is 16 bit application, we will use [DosBox](https://www.dosbox.com) to emulate a sandboxed 16 bit environment.

Copy the file to Dosbox directory, and run the file. We will see the following output,
```
C:\>hello.exe
Hello World
```

# More Ada Examples

## Example 1
TwoNum.ada contains,
```Ada
procedure TwoNum is
 a,b,c:integer;
begin
  a:= 1;
  b:= 2;
  c:= a + b;
  putln("Summation of 1 and 2 is ", c);  
end TwoNum;
```

After compilation, TwoNum.tac contains
```
PROC    _TWONUM 
_t0     =       1       
_A      =       _t0     
_t1     =       2       
_B      =       _t1     
_t2     =       _A      +       _B      
_C      =       _t2     
wrs     _s0     
wri     _C      
wrln    
ENDP    _TWONUM 
START   PROC    _TWONUM 
```

TwoNum.asm
```asm
		.model small
		.586
		.stack 100h
		.data
_s0     db      "Summation of 1 and 2 is ","$"
_t0     dw      ?       
_t1     dw      ?       
_t2     dw      ?       
_A      dw      ?       
_B      dw      ?       
_C      dw      ?       
		.code
		include io.asm

		;PROC    _TWONUM 
_TWONUM		proc
		push bp
		mov bp, sp
		sub sp, 12

		;_t0     =       1       
		mov ax, 1
		mov _t0 , ax

		;_A      =       _t0     
		mov ax, _t0
		mov _A , ax

		;_t1     =       2       
		mov ax, 2
		mov _t1 , ax

		;_B      =       _t1     
		mov ax, _t1
		mov _B , ax

		;_t2     =       _A      +       _B      
		mov ax, _A
		add ax, _B
		mov _t2 , ax

		;_C      =       _t2     
		mov ax, _t2
		mov _C , ax

		;wrs     _s0     
		mov dx, offset _s0
		call writestr

		;wri     _C      
		mov ax, _C
		call writeint

		;wrln    
		call writeln

		;ENDP    _TWONUM 
		add sp, 12
		pop bp
		ret 0
_TWONUM		ENDP

		;START   PROC    _TWONUM 
main		PROC
		mov ax, @data
		mov ds, ax
		call _TWONUM
		mov ah, 4ch
		int 21h
main		ENDP
		END main
```

Doxbox Output,
```
C:/>TwoNum
Summation of 1 and 2 is 3
```
