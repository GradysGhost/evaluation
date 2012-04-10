package evaluation;

public class Test {
	
	public static void main(String[] args) {
		System.out.println(Evaluator.infixToPostfix(args[0]));
		System.out.println(Evaluator.evaluateInfix(args[0]));
	}
	
}
