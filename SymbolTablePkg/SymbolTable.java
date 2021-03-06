package SymbolTablePkg;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Implementation of Symbol Table
 * It contains, Insert, lookup, deleteDepth, printDepth and hash function.
 * All function except hash is public.
 */
public class SymbolTable {
    public static int CurrentDepth = 0; // depth starts from 0
    public static int TableSize = 211;
    private ArrayList<LinkedList<Symbol>> _hashTable = new ArrayList<>(); // array of 'linked list of symbols'

    public SymbolTable(){
        // initialize all 211 elements as null
        for(int i = 0; i< TableSize; i++){
            _hashTable.add(null); // initialize every linkedlist as null
        }
    }

    /**
     * Lookup the symbol table using lexeme_. If found returns the symbol, otherwise returns null.
     * @param lexeme_ lexeme_ we are looking for
     * @return returns the symbol or null if not found.
     */
    public Symbol lookup(String lexeme_){
        for(int i = 0; i< TableSize; i++){
            LinkedList<Symbol> linkedList = _hashTable.get(i);
            if(linkedList != null){
                for(int j = 0; j<linkedList.size(); j++){
                    Symbol symbol = linkedList.get(j);
                    if(symbol.lexeme.equals(lexeme_)){
                        return symbol;
                    }
                }
            }
        }
        // if symbol not found, return null.
        return null;
    }

    public Symbol lookup(String lexeme_, ESymbolType desiredSymbolType_){
        for(int i = 0; i< TableSize; i++){
            LinkedList<Symbol> linkedList = _hashTable.get(i);
            if(linkedList != null){
                for(int j = 0; j<linkedList.size(); j++){
                    Symbol symbol = linkedList.get(j);
                    if(symbol.lexeme.equals(lexeme_) && symbol.getSymbolType().equals(desiredSymbolType_)){
                        return symbol;
                    }
                }
            }
        }
        // if symbol not found, return null.
        return null;
    }

    /**
     * Insert a symbol into the symbol table.
     * @param lexeme_ lexeme_ of the symbol
     * @param depth_ depth_ of the symbol
     */
    public Symbol insert(String lexeme_, int depth_){
        if(depth_ < CurrentDepth){
            System.out.println("Error inserting '" + lexeme_ + "' ,only allowed to insert at depth " + CurrentDepth);
            System.exit(0);
        }

        // generate the symbol
        Symbol symbol = new Symbol(lexeme_, depth_);

        // insert it to the hash table
        int index = hash(lexeme_);
        LinkedList<Symbol> linkedList = _hashTable.get(index);
        if(linkedList == null){
            linkedList = new LinkedList<>();
            linkedList.addFirst(symbol);
            _hashTable.set(index, linkedList);
        }
        else {
            linkedList.addFirst(symbol);
        }

        return symbol;
    }

    /**
     * Print all symbols at current depth_.
     * @param depth_ the desired depth_
     */
    public void printDepth(int depth_){
        for(int i = 0; i< TableSize; i++){
            LinkedList<Symbol> linkedList = _hashTable.get(i);
            if(linkedList != null){
                for(int j = 0; j<linkedList.size(); j++){
                    Symbol symbol = linkedList.get(j);
                    if(symbol.depth == depth_)
                        System.out.println("Symbol: Lexeme '" + symbol.lexeme + "', SymbolType " + symbol.symbolType + ", Depth " + symbol.depth +
                                "\nAttributes of '" +symbol.lexeme + "': "  + symbol.getSymbolAttributes());
                }
            }
        }
    }

    public ArrayList<Symbol> lookup(int depth_){
        ArrayList<Symbol> listOfSymbols = new ArrayList<>();
        for(int i = 0; i< TableSize; i++){
            LinkedList<Symbol> linkedList = _hashTable.get(i);
            if(linkedList != null){
                for(int j = 0; j<linkedList.size(); j++){
                    Symbol symbol = linkedList.get(j);
                    if(symbol.depth == depth_)
                        listOfSymbols.add(symbol);
                }
            }
        }

        return listOfSymbols;
    }

    /**
     * Delete a given depth_. Only the current depth_ is allowed to be deleted.
     * @param depth_ the desired depth_
     */
    public void deleteDepth(int depth_){
        if(depth_ < CurrentDepth){
            System.out.println("Can not delete lower depth.");
            System.exit(0);
        }else{
            for(int i = 0; i< TableSize; i++){
                LinkedList<Symbol> linkedList = _hashTable.get(i);
                if(linkedList != null){
                    for(int j = 0; j<linkedList.size(); j++){
                        Symbol symbol = linkedList.get(j);
                        if(symbol.depth == depth_)
                            linkedList.remove(symbol);
                    }
                }
            }
        }

    }

    /**
     * Java implement of hashpjw from P. J. Weinberger.
     * For internal use only.
     * @param lexeme_ given lexeme_
     * @return returns corresponding hash value
     */
    private int hash(String lexeme_){
        // In C unsigned int is 2 or 4 byte or 16 or 32 bit long ( we will assume 32 bit )
        // Range of 32 bit unsigned value is 0 to 4,294,967,295(2^32-1) or 0000 0000 to FFFF FFFF

        // In Java int is 32 bit; they are always signed and uses 2's complement
        // Range of 32 bit signed value is -2,147,483,648(-2^32) to 2,147,483,647(2^32-1) or 0000 0000 to FFFF FFFF
        int h = 0, g = 0;
        for(char c : lexeme_.toCharArray()){
            h = (h << 4) + c;
            g = h & 0xF000_0000; // 1111 0000 0000 0000  0000 0000 0000 0000
            long bigInt = Long.parseLong(Integer.toUnsignedString(g)); // using long since it is larger than int
            if(bigInt > 0 ){
                h = h ^ (g >> 24);
                h = h ^ g;
            }
        }
        return h % TableSize;
    }

    /**
     * Overloaded insert method
     * @param symbol_ symbol to enter into the symbol table
     * @return returns the inserted symbol. If a null symbol was passed the function would return null
     */
    public Symbol insert(Symbol symbol_) {
        if(symbol_ != null) {
            return insert(symbol_.lexeme, symbol_.depth);
        }
        return null;
    }
}
