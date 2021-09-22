package mua;

import java.util.Scanner;
import java.util.Stack;
import java.util.HashMap;


class Calculate{
    public Number cal(Number n1, Number n2, String op){
        if(op.equals("+")){
            return n2.add(n1);
        }
        else if(op.equals("-")){
            return n2.sub(n1);
        }
        else if(op.equals("*")){
            return n2.mul(n1);
        }
        else if(op.equals("/")){
            return n2.div(n1);
        }
        else     
            return n2.mod(n1);
    }

    public boolean priority(String s1, String s2){
        if((s1.equals("*") || s1.equals("/") || s1.equals("%")) && (s2.equals("+") || s2.equals("-")))
            return false;
        else if(s2.equals("(") || s2.equals(")"))
            return false;
        else
            return true;
    }

    public Value operator(Interpret inter, String op, Scanner s, HashMap<String, Value> hm){
        Value a1 = inter.getValue(s, hm);
        Value a2 = inter.getValue(s, hm);
        if(!a1.ifWord() || !a2.ifWord()){
            inter.ifError = true;
            return new ErrorValue();
        }
        if(!inter.ifNumber(a1.toString()) && !inter.ifNumber(a2.toString())){
            inter.ifError = true;
            return new ErrorValue();
        }
        Number n1 = new Number((Word)a1);
        Number n2 = new Number((Word)a2);
        Number n;
        if(op.equals("add")){
            n = n1.add(n2);
        }
        else if (op.equals("sub")){
            n = n1.sub(n2);
        }
        else if (op.equals("mul")){
            n = n1.mul(n2);
        }
        else if (op.equals("div")){
            n = n1.div(n2);
        }
        else if (op.equals("mod")){
            n = n1.mod(n2);
        }
        else{
            Value a = new ErrorValue();
            inter.ifError = true;
            //a.setType(false, false);
            return a;
        }
        return n;
    }

    public Value calExpr(Interpret inter, Scanner s, HashMap<String, Value> hm){
        int count = 1;
        Stack<String> s1 = new Stack<String>();
        Stack<Number> s2 = new Stack<Number>();
        StringBuilder _expr = new StringBuilder();
        String expr, cur;
        _expr.append("( ");
        while(s.hasNext()){
            cur = s.next();
            if(cur.equals("(")){
                count++;
            }
            else if(cur.equals(")")){
                count--;
            }
            _expr.append(cur);
            _expr.append(" ");
            if(count == 0)  break;
        }
        expr = _expr.toString();
        expr = expr.replace("+", " + ");
        expr = expr.replace("-", " -");
        expr = expr.replace("*", " * ");
        expr = expr.replace("/", " / ");
        expr = expr.replace("%", " % ");
        Scanner scan1 = new Scanner(expr);
        String last = "";
        while(scan1.hasNext()){
            String now = scan1.next();
            if(now.charAt(0) == '-'){
                //System.out.println(last);
                if(now.equals("-"));
                else if(last.equals("(") || last.equals("+") || last.equals("-") || last.equals("*") || last.equals("/") || last.equals("%")){
                //    System.out.println(111);
                    if(inter.ifNumber(now))   
                        s2.push(new Number(now));  
                    last = now;
                    continue;
                }
                else{
                    while(!s1.empty() && priority("-", s1.peek())){
                        Number n = cal(s2.pop(), s2.pop(), s1.pop());
                        s2.push(n);
                    }
                    s1.push("-");  
                    now = now.substring(1);
                }
            }
            if(now.equals("+") || now.equals("-") || now.equals("*") || now.equals("/") || now.equals("%")){
                while(!s1.empty() && priority(now, s1.peek())){
                    Number n = cal(s2.pop(), s2.pop(), s1.pop());
                    s2.push(n);
                }
                s1.push(now);  
                last = now;
            }
            else if(now.equals("(")){
                s1.push(now);
                last = now;
            }
            else if(now.equals(")")){
                while(!s1.peek().equals("(")){
                    Number n = cal(s2.pop(), s2.pop(), s1.pop());
                    s2.push(n);
                }
                s1.pop();
                last = now;
            }
            else if(inter.ifNumber(now)){
                s2.push(new Number(now));  
                last = now;
            }
            else{
                Value v = inter.getValue(now, scan1, hm);
                if(!v.ifWord()){
                    inter.ifError = true;
                }
                if(!v.ifNum()){
                    inter.ifError = true;
                }
                Word w = (Word)v;
                
                s2.push(new Number(w));
                last = w.toString();
            }
        }
        while(!s1.empty()){
            Number n = cal(s2.pop(), s2.pop(), s1.pop());
            s2.push(n);
        }
        return s2.pop();
    }
}