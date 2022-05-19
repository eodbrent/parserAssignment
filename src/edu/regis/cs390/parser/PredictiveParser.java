/*
 * Copyright (C) 2016 Richard Blumenthal, All Rights Reserved
 * Dissemination or reproduction of this code is strictly forbidden
 * unless prior written permission is obtained from Dr. Blumenthal
 */
package edu.regis.cs390.parser;

import edu.regis.cs390.tok.TokenType;
import edu.regis.cs390.tok.Token;
import edu.regis.cs390.scan.Scanner;
import java.io.IOException;

/**
 * A top-down Predictive Parser for Scott's (2016) Simple Calculator
 * LL(1) grammar.
 * 
 * @author Rickb
 */
public class PredictiveParser {
    /**
     * The most recently scanned Token.
     */
    private Token currentToken;
    
    /**
     * The lexical analysis Scanner used by this Parser
     */
    private final Scanner scanner;
    
    private TreeNode parseTree;
    
    /**
     * Initialize this Parser with the given Scanner
     * 
     * @param scanner lexical analysis Scanner that returns Tokens. 
     */
    public PredictiveParser(Scanner scanner) {
        this.scanner = scanner;
    }
    
    /**
     * Entry point that begins a parse of the input source file
     * beginning with the Program start non-terminal
     * 
     * @throws ParseError an unexpected token or IO error occurred.
     * @throws IOException an unexpected non-recoverable error occurred
     */
    public TreeNode parse() throws ParseError, IOException {
        currentToken = scanner.next();
        
        program();
        
        return parseTree;
    }
    
    /**
     * Parses the production: program -> stmt_list $$
     * 
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred
     */
    private void program() throws ParseError, IOException {
        switch(currentToken.type) {
            case ID:
            case READ:
            case WRITE:
            case EOF:
                parseTree = new TreeNode(TreeNode.PRODUCTION.PROGRAM, null);
               
                stmtList(parseTree);
                match(TokenType.EOF, parseTree);
               
                System.out.println("Parse Sucessful");
                break;
            
            default:
                throw new ParseError("program() unmatched token: " +
                                      currentToken.type);
        }
    }
    
    /**
     * Parses the production: stmtList -> stm stmtList | null
     * 
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred
     */
    private void stmtList(TreeNode parent) throws ParseError, IOException {
        TreeNode node = new TreeNode(TreeNode.PRODUCTION.STMT_LIST, parent);
        
        switch (currentToken.type) {
            case ID:
            case READ:
            case WRITE:
            case INTEGER:
            case BOOLEAN:
                stmt(node);
                stmtList(node);
                break;
                
            case EOF:
                node.add(new TreeNode(TreeNode.PRODUCTION.EMPTY, null));
                break;
                
            default:
                throw new ParseError("stmtList() unmatched token: " +
                                      currentToken.type);
        }
    }
    
    /**
     * Parses the production: stmt -> id = expr idTail | read id | write expr
     * 
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred
     */
    private void stmt(TreeNode parent) throws ParseError, IOException {
        TreeNode node = new TreeNode(TreeNode.PRODUCTION.STMT, parent);
        
        switch (currentToken.type) {
            case BOOLEAN:
                match(TokenType.BOOLEAN, node);
                declaration(node);
                break;
                
            case ID:
                match(TokenType.ID, node);
                
                idTail(node);
                break;
                
            case INTEGER:
                match(TokenType.INTEGER, node);
                declaration(node);
                break;
                
            case READ:
                match(TokenType.READ, node);    
                match(TokenType.ID, node);
                break;
                
            case WRITE:
                match(TokenType.WRITE,node);
                
                expr(node);
                break;
     
                
            default:
                throw new ParseError("stmt() unmatched token: " +
                                      currentToken.type);
        }
    }
    /**
     * Parses the production: idTail -> expr id
     * 
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred
     */
    private void idTail(TreeNode parent) throws ParseError, IOException {
        TreeNode node = new TreeNode(TreeNode.PRODUCTION.EXPR, parent);
        
        switch (currentToken.type) {
            case ASSIGN:
                match(TokenType.ASSIGN, node);
                expr(node);       
                break;
            case LPAREN:
                match(TokenType.LPAREN, node);
                match(TokenType.ID, node);
                match(TokenType.RPAREN, node);
                break;
            default:
               throw new ParseError("expr() unmatched token: " +
                                      currentToken.type); 
        }
    }
    /**
     * Parses the production: idTail -> expr id
     * 
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred
     */
    private void declaration(TreeNode parent) throws ParseError, IOException {
        TreeNode node = new TreeNode(TreeNode.PRODUCTION.EXPR, parent);
        
        switch (currentToken.type) {
            case INTEGER:
                match(TokenType.INTEGER, node);
                expr(node);       
                break;
                
            case BOOLEAN:
                match(TokenType.BOOLEAN, node);
                expr(node);
                break;
                
            default:
               throw new ParseError("expr() unmatched token: " +
                                      currentToken.type); 
        }
    }
    /**
     * Parses the production: expr -> term termTail
     * 
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred
     */
    private void expr(TreeNode parent) throws ParseError, IOException {
        TreeNode node = new TreeNode(TreeNode.PRODUCTION.EXPR, parent);
        
        switch (currentToken.type) {
            case ID:
            case NUMBER:
            case TRUE:
            case FALSE:
            case LPAREN:
            case NOT:
                term(node);
                        
                termTail(node);
                break;

            default:
               throw new ParseError("expr() unmatched token: " +
                                      currentToken.type); 
        }
    }
    
    /**
     * Parses the production: term -> factor factorTail
     * 
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred
     */
    private void term(TreeNode parent) throws ParseError, IOException {
        TreeNode node = new TreeNode(TreeNode.PRODUCTION.TERM, parent);
        
        switch (currentToken.type) {
            case LPAREN:
            case ID:
            case TRUE:
            case FALSE:
            case NUMBER:
            case NOT:
                factor(node);
                factorTail(node);
                break;
              
            default:
                throw new ParseError("term() unmatched token: " +
                                      currentToken.type);
        }
    }
    
    /**
     * Parses the production: termTial -> addOp term termTail | null
     * 
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred     */
    private void termTail(TreeNode parent) throws ParseError, IOException {
        TreeNode node = new TreeNode(TreeNode.PRODUCTION.TERM_TAIL, parent);
        
       switch (currentToken.type) {
           case PLUS:
           case MINUS:
               addOp(node);
               term(node);
               termTail(node);
               break;
           case RPAREN:
           case ID:
           case READ:
           case WRITE:
           case EOF:
               node.add(new TreeNode(TreeNode.PRODUCTION.EMPTY, null));
               break;
           
           default:
               throw new ParseError("termTail() unmatched token: " +
                                      currentToken.type);
                      
       } 
    }
    /**
     * Parses the production: factor -> ( expr ) | id | number
     * 
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred
     */
    private void factor(TreeNode parent) throws ParseError, IOException {
        TreeNode node = new TreeNode(TreeNode.PRODUCTION.FACTOR, parent);
        
        switch (currentToken.type) {
            case ID:
                match(TokenType.ID, node);
                break;
            case NUMBER:
                match(TokenType.NUMBER, node);
                break;
            case LPAREN:
                match(TokenType.LPAREN, node);
                expr(node);
                match(TokenType.RPAREN, node);
                break;
            case TRUE:
                match(TokenType.TRUE, node);
                break;
            case FALSE:
                match(TokenType.FALSE, node);
                break;
            case NOT:
                match(TokenType.NOT, node);
                expr(node);
                break;
            default:
                throw new ParseError("factor() unmatched token: " +
                                      currentToken.type);
        }
    }
    
    /**
     * Parses production: factorTail -> multOp factor factorTail | null
     * 
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred
     */
    private void factorTail(TreeNode parent) throws ParseError, IOException {
        TreeNode node = new TreeNode(TreeNode.PRODUCTION.FACTOR_TAIL, parent);
        
        switch(currentToken.type) {
            case MULTIPLY:
            case DIVIDE:
                multOp(node);
                factor(node);
                factorTail(node);
                break;
                
            case PLUS:
            case MINUS:
            case ID:
            case READ:
            case WRITE:
            case NOT:
            case EOF:
                node.add(new TreeNode(TreeNode.PRODUCTION.EMPTY, null));
                break;
                
            default:
                throw new ParseError("factorTail() unmatched token: " +
                                      currentToken.type);
        }
    }
    
    /**
     * Parses the production: addOp -> + | -
     * 
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred 
     */
    private void addOp(TreeNode parent) throws ParseError, IOException {
        TreeNode node = new TreeNode(TreeNode.PRODUCTION.ADD_OP, parent);
        
        switch (currentToken.type) {
            case PLUS:
                match(TokenType.PLUS, node);
                break;
            case MINUS:
                match(TokenType.MINUS, node);
                break;
            default:
                throw new ParseError("addOp() unmatched token: " +
                                      currentToken.type);
        }
    }
    
    /**
     * Parses the production: multOp -> * | /
     * 
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred 
     */
    private void multOp(TreeNode parent) throws ParseError, IOException {
        TreeNode node = new TreeNode(TreeNode.PRODUCTION.MULT_OP, parent);
        
        switch (currentToken.type) {
            case MULTIPLY:
                match(TokenType.MULTIPLY, node);
                break;
            case DIVIDE:
                match(TokenType.DIVIDE, node);
                break;
            default:
                throw new ParseError("multOp() unmatched token: " +
                                      currentToken.type);
        }  
    }
    
    /**
     * If the current token has the given token type, read the next token,
     * otherwise, a ParseError is thrown.
     * 
     * @param type the expected token at this point in the parse
     * @throws ParseError the expected token wasn't found
     * @throws IOException an unexpected non-recoverable error occurred
     */
    private TreeNode match (TokenType type, TreeNode parent) 
            throws ParseError, IOException {
        TreeNode node = null;
        
        if (currentToken.type == type) {
            switch (type) {
                case ID:
                    node = new TreeNode(TreeNode.PRODUCTION.ID, parent, currentToken.lexeme);
                    break;
                case NUMBER:
                    node = new TreeNode(TreeNode.PRODUCTION.NUMBER, parent, currentToken.lexeme);
                    break;
                case ASSIGN:
                    node = new TreeNode(TreeNode.PRODUCTION.PUNCTUATION , parent, ":=");
                    break;
                case NOT:
                    node = new TreeNode(TreeNode.PRODUCTION.PUNCTUATION , parent, "!");
                    break;
                case TRUE:
                    node = new TreeNode(TreeNode.PRODUCTION.TRUE , parent, currentToken.lexeme);
                    break;
                case FALSE:
                    node = new TreeNode(TreeNode.PRODUCTION.FALSE , parent, currentToken.lexeme);
                    break;
                case EOF:
                    node = new TreeNode(TreeNode.PRODUCTION.PUNCTUATION, parent, "$$");
                    break;
                case READ:
                case WRITE:
                    node = new TreeNode(TreeNode.PRODUCTION.KEYWORD,
                                        parent,
                                        currentToken.lexeme);
                    break;
                default:
                     node = new TreeNode(TreeNode.PRODUCTION.PUNCTUATION,
                                        parent,
                                        currentToken.lexeme.toUpperCase());
                    break;
            }
            
            currentToken = scanner.next();
            
            return node;
        } else {
            throw new ParseError(type, scanner.getLineNo());
        }
    }
}

/*
TESTED GOOD---``````````````````add true and false alternate right-side productions to the factor production
NEED TO TEST--------add “int id;” and “bool id;” alternatives to the stmt production
add a semi-colon that ends each alternative stmt production
NEED TO TEST--------add the declarations production
NEED TO TEST--------``````````````````add a “! expr” alternative production to the factor production
NEED TO TEST--------add a new idTail production that handles assignment or a function call
NEED TO TEST--------modify the stmt production to use the new idTail production
*/
