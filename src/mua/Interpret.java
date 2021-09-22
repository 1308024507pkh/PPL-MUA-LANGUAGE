package mua;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.io.*;

class Interpret{
	HashMap<String, Value> map;
	HashSet<String> funcName = new HashSet<>(); //new add
	HashMap<String, HashMap<String, Value>> localMap = new HashMap<>();
    Scanner scanInput;
    Scanner scan;
    boolean ifExit = false;
    boolean ifError = false;

    public Interpret(){
        map = new HashMap<String, Value>();
        scanInput = new Scanner(System.in);
    }

    public void addAlreadyName(HashMap<String, Value> mm){
        String name = "pi";
        Number n = new Number(3.14159);
        mm.put(name, n);
    }

    public String stringProcess(String str){
        String _str;
        _str = str.replace("(", " ( ");
        _str = _str.replace(")", " ) ");
        _str = _str.replace("[", " [ ");
        _str = _str.replace("]", " ] ");
        return _str;
    }

    public void begin(){
        map = new HashMap<String, Value>();
        addAlreadyName(map);
        scanInput = new Scanner(System.in);
        while(scanInput.hasNextLine()){
            String _in = scanInput.nextLine();
            String in = stringProcess(_in);
            scan = new Scanner(in);
            if(scan.hasNext())  getValue(scan, map);
            if(ifError){
                System.out.println("There exit some errors.");
            }
            ifError = false;
        }
        scan.close();
        scanInput.close();
    }

    public boolean ifNumber(String str){
        if(str == null)
            return false;
        Pattern pat = Pattern.compile("^-?\\d+(\\.\\d+)?$");
        return pat.matcher(str).matches();
    }

    public Value runList(MList l, HashMap<String, Value> hm){
		String _content = l.toString();
		String content = _content.substring(1, _content.length()-1);
		content = stringProcess(content);
		Value ret = l;
		Scanner scan1 = new Scanner(content);
		while(scan1.hasNext()){
			ret = getValue(scan1, hm);
			if(ret.getRetFlag())	break;
		}
		scan1.close();
		return ret;
       
    }

	//new modify
    public Value getValue(Scanner s, HashMap<String, Value> hm){
        //System.out.println(1);
        while(!s.hasNext()){
            if(s==scan && scanInput.hasNextLine()){
				String _in = scanInput.nextLine();
            	String in = stringProcess(_in);
				scan = new Scanner(in);
				s = scan;
			}
			else	return new Word("");
		}
		
		String op = s.next();
        //System.out.println(op);
        return getValue(op, s, hm);
    }

    public Value getValue(String op, Scanner s, HashMap<String, Value> hm){
        
        if (op.charAt(0) == '\"'){
            Word _word = new Word(op.substring(1));
            if(op.substring(1).equals("false") || op.substring(1).equals("true"))
                _word.setType(true, false);
            if(ifNumber(op.substring(1)))
                _word.setType(false, true);
            return _word;
        }

        else if (op.equals("false") || op.equals("true")){
            BooLean _boolean = new BooLean(op);
            return _boolean;
        }

        else if(op.equals("[")){
            int count = 1, num = 1, index = -1;
            StringBuilder _expr = new StringBuilder();
            String expr, cur;
			_expr.append("[ ");
            while(true){
				while(!s.hasNext()){
					if(s==scan && scanInput.hasNextLine()){					
						String _in = scanInput.nextLine();
						String in = stringProcess(_in);
						scan = new Scanner(in);
						s = scan;
					}
					else    return new ErrorValue();//{System.out.println("err18");return new ErrorValue();}
				}
                cur = s.next();
                if(cur.equals("[")){
                    num++; count++;
                }
                else if(cur.equals("]")){
                    count--;
                }
                if(count == 0)  break;
                _expr.append(cur);
                _expr.append(" ");
            }
            MList[] listArray = new MList[num];
            expr = _expr.toString();
            Scanner scan1 = new Scanner(expr);
            while(scan1.hasNext()){
                String now = scan1.next();
                if(now.equals("[")){
                    index++;
                    listArray[index] = new MList();
                }
                else if(now.equals("]")){
                    if(index>0)
                        listArray[index-1].addValue(listArray[index]);
                    index--;
                }
                else{
                    listArray[index].addValue(new Word(now));
                }
            }
            scan1.close();
            return listArray[0];
        }

        else if (ifNumber(op)){
            Number _number = new Number(op);
            return _number;
        }

        else if (op.equals("make")){
            Value _name = getValue(s, hm);
            if(!_name.ifWord()) ifError = true;//{System.out.println("err16");ifError = true;}
            if(!ifError && _name.ifName() == false){
                ifError = true;//System.out.println("err17");
            }
            Value _value = getValue(s, hm);
            if(ifError) return new ErrorValue();//{System.out.println("88882");}
            hm.put(_name.toString(), _value);
			//if(_value.ifFunc()){funcName.add(_name.toString());map.put(_name.toString(), _value);}
            return _value;
        }

        else if (op.equals("thing")){
            Value _name = getValue(s, hm);
            if(!_name.ifWord()){
                ifError = true;
                //System.out.println("err15");
                return new ErrorValue();
            }
			String name = _name.toString();
			if(!hm.containsKey(name)){
				if(!map.containsKey(name)){
                    ifError = true;
                    //System.out.println("err14");
					return new ErrorValue();
				} 
				else{
					Value _value = map.get(name);
                	return _value;
				}
			}
            else{
                Value _value = hm.get(name);
                return _value;
            }
        }
        else if (op.charAt(0) == ':'){
			String name = op.substring(1);
			if(!hm.containsKey(name)){
				if(!map.containsKey(name)){
                    ifError = true;
                    //System.out.println("err13");
					return new ErrorValue();
				}
				else{
                 //   System.out.println(11111);
					Value _value = map.get(name);
                	return _value;
				}
			}
            else{
                Value _value = hm.get(name);
                return _value;
            }
        }

        else if (op.equals("print")){
            Value _value = getValue(s, hm);
            String str = _value.toString();
            if(ifError)     return new ErrorValue();//{System.out.println("err12");return new ErrorValue();}
            if(_value.ifList()) System.out.println(str.substring(1, str.length()-1));
            else    System.out.println(_value);
            return _value;
        }

        else if (op.equals("read")){
            scan = new Scanner(System.in);//  Scanner readScan = new Scanner(System.in);
            String str = new String("");
	        if(scanInput.hasNextLine())
            	str = scanInput.nextLine();
           // readScan.close();
            return new Word(str);
        }

        else if (op.equals("add") || op.equals("sub") || op.equals("mul") || op.equals("div") || op.equals("mod")){
            Calculate ca = new Calculate();
            return ca.operator(this, op, s, hm);
        }

        else if (op.equals("erase")){
            Value _name = getValue(s, hm);
            Value _value;
            if(!_name.ifWord()){
                ifError = true;
                //System.out.println("err11");
                return new ErrorValue();
            }
            Iterator<String> iter = hm.keySet().iterator();
            while(iter.hasNext()){
                if(iter.next().equals(_name.toString())){
                    _value = hm.get(_name.toString());
                    iter.remove();
                    return _value;
                }
            }
            ifError = true;
            //System.out.println("err10");
            return new ErrorValue();
        }

        else if (op.equals("isname")){
            Value _name = getValue(s, hm);
            if(!_name.ifWord()){
                return new BooLean(false);
            }
            Iterator<String> iter = hm.keySet().iterator();
            while(iter.hasNext()){
                if(iter.next().equals(_name.toString())){
                    return new BooLean(true);
                }
            }
            return new BooLean(false);
        }

        else if (op.equals("run")){
            Value v = getValue(s, hm);
            if(!v.ifList()){
                ifError = true;
                //System.out.println("err10");
                return new ErrorValue();
            }
            return runList((MList)v, hm);
            
        }

        else if (op.equals("eq") || op.equals("gt") || op.equals("lt")){
            Value v1 = getValue(s, hm);
            Value v2 = getValue(s, hm);
            /*
            if(!v1.ifWord() || !v2.ifWord()){
                ifError = true;
                //System.out.println("err9");
                return new ErrorValue();
            }*/
            //System.out.println(v1.toString());System.out.println(v2.toString());
            /*
            if(op.equals("eq")){
                if(v1.toString().equals(v2.toString()))
                    return new BooLean(true);
                else    
                    return new BooLean(false);
            }*/
            if(!ifNumber(v1.toString()) || !ifNumber(v2.toString())){
                if(op.equals("gt")){
                    if (v1.toString().compareTo(v2.toString()) > 0)
                        return new BooLean(true);
                    else 
                        return new BooLean(false);
                }
                else if(op.equals("lt")){
                    if (v1.toString().compareTo(v2.toString()) < 0)
                        return new BooLean(true);
                    else 
                        return new BooLean(false);
                }
                else{
                    if(v1.toString().equals(v2.toString()))
                        return new BooLean(true);
                    else    
                        return new BooLean(false);
                }
            }
            else{
                Number n1 = new Number((Word)v1);
                Number n2 = new Number((Word)v2);
                if(op.equals("gt")){
                    return new BooLean(n1.getVal() > n2.getVal());
                }
                else if(op.equals("lt")){
                    return new BooLean(n1.getVal() < n2.getVal());
                }
                else{
                    return new BooLean(n1.getVal() == n2.getVal());
                }
            }

        }

        else if(op.equals("and") || op.equals("or")){
            Value v1 = getValue(s, hm);
            Value v2 = getValue(s, hm);
            if(!v1.ifBooLean() || !v2.ifBooLean()){
                ifError = true;
                //System.out.println("err8");
                return new ErrorValue();
            }
            BooLean b1 = new BooLean((Word)v1);
            BooLean b2 = new BooLean((Word)v2);
            if(op.equals("and")){
                if(b1.getVal()==true && b2.getVal()==true)
                    return new BooLean(true);
                else
                    return new BooLean(false);
            }
            else{
                if(b1.getVal()==true || b2.getVal()==true)
                    return new BooLean(true);
                else
                    return new BooLean(false);
            }
        }

        else if(op.equals("not")){
            Value v = getValue(s, hm);
            if(!v.ifBooLean()){
                ifError = true;
                //System.out.println("err7");
                return new ErrorValue();
            }
            BooLean b = new BooLean((Word)v);
            if(b.getVal()==false)
                return new BooLean(true);
            else
                return new BooLean(false);
        }

        else if(op.equals("isnumber")){
            Value v = getValue(s, hm);
            return new BooLean(v.ifNum());
        }

        else if(op.equals("isword")){
            Value v = getValue(s, hm);
            if(v.ifWord() && !(v.ifBooLean() | v.ifNum()))
                return new BooLean(true);
            return new BooLean(false);
        }

        else if(op.equals("islist")){
            Value v = getValue(s, hm);
            return new BooLean(v.ifList());
        }

        else if(op.equals("isbool")){
            Value v = getValue(s, hm);
            return new BooLean(v.ifBooLean());
        }

        else if(op.equals("isempty")){
            Value v = getValue(s, hm);
            if(v.ifWord()){
                return new BooLean(v.toString().equals(""));
            }
            else if(v.ifList()){
                MList l = (MList)v;
                return new BooLean(l.value.isEmpty());
            }
            else{
                ifError = true;
                //System.out.println("err6");
                return new ErrorValue();
            }
        }

        else if(op.equals("if")){
            Value v = getValue(s, hm);
            if(!v.ifBooLean()){
                ifError = true;
                //System.out.println("err5");
                return new ErrorValue();
            }
            Value v1 = getValue(s, hm);
            Value v2 = getValue(s, hm);
            if(!v1.ifList() || !v2.ifList()){
                ifError = true;
                //System.out.println("err4");
                return new ErrorValue();
            }
            BooLean b = (BooLean)v;
            MList l1 = (MList)v1, l2 = (MList)v2;
            if((b.getVal() == true)){
                if(l1.value.isEmpty()){
                    return l1;
                }
                return runList(l1, hm);
            }
            else{
                if(l2.value.isEmpty()){
                    return l2;
                }
                return runList(l2, hm);
            }
        }

        else if(op.equals("(")){
            Calculate ca = new Calculate();
            return ca.calExpr(this, s, hm);
        }

		else if(hm.containsKey(op) && hm.get(op).ifFunc()){
            //System.out.println("222");
            Value v = hm.get(op);
			return callFunc(op, v, s, hm);
		}

		else if(op.equals("return")){
			//System.out.println("111");
			Value v =  getValue(s, hm);
			v.setRetFlag(true);
			return v;
		}

		else if(op.equals("export")){
            Value _name = getValue(s, hm);
            //System.out.println("99"+_name.toString());
			if(!_name.ifWord()){
                ifError = true;
                //System.out.println("err3");
				return new ErrorValue();
			}
			String name = _name.toString();
			if(!hm.containsKey(name)){
               // System.out.println("996"+_name.toString());
                ifError = true;
                //System.out.println("err2");
				return new ErrorValue();
			}
			Value nameVal = hm.get(name);
			if(hm!=map){
                map.put(name, nameVal);
             //   System.out.println(nameVal.toString());
            }
			return nameVal;
		}

        else if(op.equals("readlist")){
            //scan = new Scanner(System.in);//  Scanner readScan = new Scanner(System.in);
            String str = new String("");
	        if(scanInput.hasNextLine())
                str = scanInput.nextLine();
            MList l = new MList();
            Scanner ss = new Scanner(str);
            while(ss.hasNext()){
                String curS = ss.next();
                l.addValue(new Word(curS));
            }
           // readScan.close();
            return l;
        }

        else if(op.equals("word")){
            Value v1 = getValue(s, hm);
            Value v2 = getValue(s, hm);
            if(!(v1.ifWord() && !v1.ifBooLean() && !v1.ifNum() && v2.ifWord())){
                ifError = true;
                return new ErrorValue();
            }
            String str = v1.toString() + v2.toString();
            return new Word(str);
        }

        else if(op.equals("sentence")){
            int i;
            Value v1 = getValue(s, hm);
            Value v2 = getValue(s, hm);
            MList l = new MList();
            if(v1.ifList()){
                MList l1 = (MList)v1;
                for(i=0; i<l1.value.size(); i++)
                    l.addValue(l1.value.get(i));
            }
            else{
                l.addValue(v1);
            }
            if(v2.ifList()){
                MList l2 = (MList)v2;
                for(i=0; i<l2.value.size(); i++)
                    l.addValue(l2.value.get(i));
            }
            else{
                l.addValue(v2);
            }
            return l;
        }

        else if(op.equals("list")){
            Value v1 = getValue(s, hm);
            Value v2 = getValue(s, hm);
            MList l = new MList();
            l.addValue(v1);
            l.addValue(v2);
            return l;
        }

        else if(op.equals("join")){
            Value v1 = getValue(s, hm);
            Value v2 = getValue(s, hm);
            if(!v1.ifList()){
                ifError = true;
                return new ErrorValue();
            }
            MList l = (MList)v1;
            l.addValue(v2);
            return l;
        }

        else if(op.equals("first")){
            Value v = getValue(s, hm);
            return v.first();
        }

        else if(op.equals("last")){
            Value v = getValue(s, hm);
            return v.last();
        }

        else if(op.equals("butfirst")){
            Value v = getValue(s, hm);
            return v.butfirst();
        }

        else if(op.equals("butlast")){
            Value v = getValue(s, hm);
            return v.butlast();
        }

        else if(op.equals("random")){
            Value v = getValue(s, hm);
            if(v.ifNum()){
                ifError = true;
                return new ErrorValue();
            }
            Number n = (Number)v;
            double curV = Math.random()*(n.getVal());
            return new Number(curV);
        }

        else if(op.equals("int")){
            Value v = getValue(s, hm);
            if(v.ifNum()){
                ifError = true;
                return new ErrorValue();
            }
            Number n = (Number)v;
            double curV = Math.floor(n.getVal());
            return new Number(curV);
        }

        else if(op.equals("sqrt")){
            Value v= getValue(s, hm);
            if(v.ifNum()){
                ifError = true;
                return new ErrorValue();
            }
            Number n = (Number)v;
            double curV = Math.sqrt(n.getVal());
            return new Number(curV);
        }

        else if(op.equals("save")){
          try{
            Value v = getValue(s, hm);
            if(!v.ifWord()){
                ifError = true;
                return new ErrorValue();
            }
            String fileName = v.toString();
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            for(String str:hm.keySet()){
                String ss = "make \"" + str + " ";
                Value v1 = hm.get(str);
                if(v1.ifList()){
                    ss += v1.toString();
                }
                else{
                    ss = ss+ "\"" + v1.toString();
                }
                out.write(ss);
                out.newLine();
            }
            out.close();
            return v;
           }catch(Exception e){
               System.out.println("SB!");
               return new ErrorValue();
           }
        }

        else if(op.equals("load")){
          try{
            Value v = getValue(s, hm);
            BufferedReader br = new BufferedReader(new FileReader(v.toString()));
            String line;
            Scanner scan_1;
            while ((line = br.readLine()) != null) {
                String in = stringProcess(line);
                //System.out.println(in);
                scan_1 = new Scanner(in);
                if(scan_1.hasNext())  getValue(scan_1, map);
            }
            return new BooLean(true);
          } catch(Exception e){
            //System.out.println("bbbb");
            return new BooLean(true);
          }
            /*
            scanInput = new Scanner(System.in);
            while(scanInput.hasNextLine()){
                String _in = scanInput.nextLine();
                String in = stringProcess(_in);
                scan = new Scanner(in);
                if(scan.hasNext())  getValue(scan, map);
                if(ifError){
                    System.out.println("There exit some errors.");
                }
                ifError = false;
            }
            scan.close();
            scanInput.close();
            */
        }

        else if(op.equals("erall")){
            hm.clear();
            return new BooLean(true);
        }

        else if(op.equals("poall")){
            MList l = new MList();
            for(String str:hm.keySet()){
                Word w = new Word(str);
                l.addValue(w);
            }
            return l;
        }

        else{
            ifError = true;
            //System.out.println(op+"err1");
            return new ErrorValue();
		}
		
    }

	public Value callFunc(String op, Value val, Scanner s, HashMap<String, Value> hm) {
		MList ml = (MList)(hm.get(op));
		MList paras = (MList)(ml.value.get(0));
		MList body = (MList)(ml.value.get(1));
        HashMap<String, Value> localVar = new HashMap<String, Value>();
        addAlreadyName(localVar);
        localVar.put(op, val);
        Value v1;
		String paraStr = paras.toString();
		if(!paraStr.equals("[]")){
			Scanner paraScan = new Scanner(paraStr.substring(1, paraStr.length()-1));
			while(paraScan.hasNext()){
				String paraName = paraScan.next();
				Value v = getValue(s, hm);
				localVar.put(paraName, v);
			}
        }
        if(!body.toString().equals("[]"))
            v1 = runList(body, localVar);
        else  v1 = body;
		localMap.put(op, localVar);
        return v1;
        
	}
}






