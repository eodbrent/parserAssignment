/*
 * Copyright (C) 2016 Richard Blumenthal, All Rights Reserved
 * Dissemination or reproduction of this code is strictly forbidden
 * unless prior written permission is obtained from Dr. Blumenthal
 */
package edu.regis.cs390.parser;

import java.util.ArrayList;

/**
 * A node appearing in a parse tree. 
 * 
 * @author Rickb
 */
public class TreeNode {
    /**
     * Internal nodes correspond to a named LHS production, while leaf
     * nodes may be a punctuation, keyword (e.g. READ, WRITE), or an
     * empty string in a RHS
     */
    public enum PRODUCTION {PROGRAM, READ, STMT_LIST, STMT, INTEGER, BOOLEAN,
                            EXPR, TERM_TAIL, TERM, FACTOR_TAIL, FACTOR,
                            ADD_OP, MULT_OP, ID, IDTAIL, DECLARATION, NUMBER, TRUE, FALSE, 
                            PUNCTUATION, KEYWORD, EMPTY};
    
    /**
     * The production associated with this node (see PRODUCTION)
     */
    public final PRODUCTION type;
     
    /**
     * The lexeme associated with this node, if any
     */
    public final String lexeme;
  
    /**
     * The name of this node, which is derived.
     */
    public final String name;
    
    /**
     * Convenience pointer to the parent of this node, if any.
     */
    public final TreeNode parent;
    
    /**
     * The children of this node, which will size zero, if there are none.
     */
    private ArrayList<TreeNode> children;
    
    /**
     * Create this node with the given type, parent, and an empty lexeme
     * @param type
     * @param parent 
     */
    public TreeNode(PRODUCTION type, TreeNode parent) {
        this(type, parent, "");
    }
    
    /**
     * Create this node with the given type, parent, and lexeme
     * @param type
     * @param parent
     * @param lexeme 
     */
    public TreeNode(PRODUCTION type, TreeNode parent, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
        this.parent = parent;
        
        children = new ArrayList<>();
        
        name = buildName(type);
        
        if (parent != null)
            parent.add(this);
    }
    
    /**
     * Return this node's production type
     * @return a PRODUCTION (see PRODUCTION)
     */
    public String getProduction() {
        return name;
    }
    
    /**
     * Add the given child to this node
     * 
     * @param child 
     */
    public void add(TreeNode child) {
        children.add(child);
    }
    
    /**
     * Output this node with no indentation
     */
    public void prettyPrint() {
        prettyPrint(0);
    }
    
    /**
     * Output this node prefixed with the given indentation
     * 
     * @param indent number of spaces to indent
     */
    public void prettyPrint(int indent) {
        for (int i = 0; i < indent; i++)
            System.out.print(" ");
        
        System.out.println(name);
        indent++;
        
        for (TreeNode node : children)
            node.prettyPrint(indent);
    }
    
    /**
     * Return this node's children
     * 
     * @return 
     */
    public ArrayList<TreeNode> getChildren() {
        return children;
    }
    
    /**
     * Derive this node's name from its production type
     * 
     * @param type
     * @return internal nodes are the name of the LHS production
     *   a leaf will have some form of the lexeme
     */    
    private String buildName(PRODUCTION type) {
        switch (type) {
        case ADD_OP:
            return "addOp";
                
        case BOOLEAN:
            return "BOOLEAN(" + lexeme + ")";
            
        case DECLARATION:
            return "declaration";
            
        case EMPTY: // SPecial
            return "e";
            
        case EXPR:
            return "expr";
            
        case FACTOR_TAIL:
            return "factorTail";
            
        case FACTOR:
            return "factor";
            
        case FALSE:
            return "FALSE(" + lexeme + ")";
            
        case ID:
            return "ID(" + lexeme +")";
            
        case IDTAIL:
            return "idTail";
            
        case INTEGER:
            return "INTEGER(" + lexeme + ")";
            
        case KEYWORD:
            return "KEYWORD(" + lexeme +")";
            
        case MULT_OP:
            return "multOp";
            
        case NUMBER:
            return "NUMBER(" + lexeme + ")";
            
        case PROGRAM:
            return "program";
        
        case PUNCTUATION:
            return lexeme;
            
        case STMT:
            return "stmt";

        case STMT_LIST:
            return "stmtList";
        
        case TERM_TAIL:
            return "termTail";
            
        case TERM:
            return "term";
            
        case TRUE:
            return "TRUE(" + lexeme + ")"; 
            
        default:
            return "Illegal";
        }
    }
}
