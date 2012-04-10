/*
 *  This file is released as-is under the GNU General Public License version 3.0
 *                              For the full implications of this license, visit
 *                           http://www.gnu.org/licenses/gpl-3.0-standalone.html
 *                           Produced for the University of Texas at San Antonio
 *
 * agent.Agent
 *     author: Ryan J. Jung
 *     last modified: 05-27-2011
 *
 * This class contains static functions that collaborate to solve mathematical
 * expressions.  This is accomplished by converting infix notation (1 + 2) into
 * postfix notation (1 2 +), then sequentially solving the operations in the
 * expression.
 *
 */

package evaluation;

import java.lang.Character;
import java.lang.String;
import java.util.Stack;
import java.util.EmptyStackException;

public class Evaluator {
	
	static final String VALID_OPERATORS = "^/*%+-";
	
	private static String getValue(String input, int startAt) {
		String value = "";
		boolean valueStarted = false;
		for (int x = startAt; x < input.length(); ++x) {
			char c = input.charAt(x);
			if (valueStarted) {
				if (!Character.isDigit(c)) {
					return value;
				} else {
					value += c;
				}
			} else {
				if (Character.isDigit(c)) {
					value += c;
					valueStarted = true;
				}
			}
		}
		return value;
	}
	
	private static char getOperator(String input, int startAt) {
		String value = "";
		for (int x = startAt; x < input.length(); ++x) {
			if (VALID_OPERATORS.indexOf(input.charAt(x)) >= 0)
				return input.charAt(x);
		}
		return ' ';
	}
	
	private static String getParenthetical(String input, int startAt) {
		String value = "";
		int nestDepth = 0;
		
		for (int x = startAt; x < input.length(); ++x) {
			if (input.charAt(x) == ')') --nestDepth;
			if (nestDepth == 0 && input.charAt(x) == ')') {
				return value;
			} else if (nestDepth > 0) {
				value += input.charAt(x);
			}
			if (input.charAt(x) == '(') ++nestDepth;
		}
		
		return value;
	}

	private static char nextUsefulChar(String input, int startAt) {
		for (int x = startAt; x < input.length(); ++x)
			if (!Character.isWhitespace(input.charAt(x))) return input.charAt(x);
		
		return ' ';
	}
	
	public static String infixToPostfix(String infix) {
		Stack operators = new Stack();
		int i = 0;
		String postfix = "";
		String value = "";
		char operator = ' ';
		char topOperator = ' ';
		
		while (i < infix.length()) {
			char c = nextUsefulChar(infix, i);
			if (VALID_OPERATORS.indexOf(c) >= 0) {
				// Next character is an operator
				operator = getOperator(infix, i);
				i = infix.indexOf(operator, i) + 1;
				if (!operators.empty()) {
					topOperator = operators.peek().toString().charAt(0);
					String toPop = "^";  // Holds all operators of higher or equal precedence as the current operator
					if (operator == '/' || operator == '*' || operator == '%') toPop = "^/*%";
					if (operator == '+' || operator == '-') toPop = "^/*%+-";
					while (!operators.empty() && toPop.indexOf(topOperator) >= 0)
						postfix += operators.pop().toString().charAt(0) + " ";
				}
				operators.push(operator);
			} else {
				// Next character starts a value or parenthetical (or ends a paren)
				if (c == '(') {
					String parenContent = getParenthetical(infix, i);
					i += parenContent.length() + 2;
					postfix += infixToPostfix(parenContent) + " ";
				} else if (c == ')') {
					++i;
				} else {
					value = getValue(infix, i);
					i = infix.indexOf(value, i) + value.length();
					postfix += value + " ";
				}
			}
		}
		
		while (!operators.empty())
			postfix += operators.pop().toString().charAt(0) + " ";
		
		return postfix.trim();
	}
	
	public static double evaluatePostfix(String postfix) {
		Stack operands = new Stack();
		String value;
		double o1, o2;
		int i = 0;
		
		while (i < postfix.length()) {
			char c = nextUsefulChar(postfix, i);
			if (VALID_OPERATORS.indexOf(c) >= 0) {
				char operator = getOperator(postfix, i);
				i = postfix.indexOf(operator, i) + 1;
				try {
					o2 = Double.parseDouble(operands.pop().toString());
					o1 = Double.parseDouble(operands.pop().toString());
					switch (operator) {
						case '^':
							o2 = Math.pow(o1, o2);
							break;
						case '*':
							o2 = o1 * o2;
							break;
						case '/':
							o2 = o1 / o2;
							break;
						case '%':
							o2 = o1 % o2;
							break;
						case '+':
							o2 = o1 + o2;
							break;
						case '-':
							o2 = o1 - o2;
							break;
					}
					operands.push(o2);
				} catch (EmptyStackException ex) {
					ex.printStackTrace();
				}
			} else {
				value = getValue(postfix, i);
				i = postfix.indexOf(value, i) + value.length();
				operands.push(value);
			}
		}
		
		return Double.parseDouble(operands.pop().toString());
	}
	
	public static double evaluateInfix(String infix) {
		return evaluatePostfix(infixToPostfix(infix));
	}
	
}
