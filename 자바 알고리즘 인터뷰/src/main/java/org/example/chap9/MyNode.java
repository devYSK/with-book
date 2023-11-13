package org.example.chap9;

import lombok.Getter;

public class MyNode {

	private int item;

	private MyNode next;

	public MyNode(final int item, final MyNode next) {
		this.item = item;
		this.next = next;
	}

	public int getItem() {
		return item;
	}

	public MyNode getNext() {
		return next;
	}
}
