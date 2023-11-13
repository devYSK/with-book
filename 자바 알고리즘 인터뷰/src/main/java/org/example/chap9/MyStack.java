package org.example.chap9;

import java.util.Stack;

import lombok.Getter;

@Getter
public class MyStack {

	private MyNode last;

	public MyStack() {
		this.last = null;
	}


	public void push(int item) {
		this.last = new MyNode(item, this.last);
	}

	public int pop() {
		int item = this.last.getItem();

		this.last = this.last.getNext();

		return item;
	}

	public static void main(String[] args) {
		final MyStack myStack = new MyStack();

		for (int i = 0; i < 5; i++) {
			myStack.push(i + 1);
		}


		while (myStack.last != null) {
			System.out.println(myStack.pop());
		}

		new Stack<>();
	}
}
