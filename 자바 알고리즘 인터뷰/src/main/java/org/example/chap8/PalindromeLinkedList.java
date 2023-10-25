package org.example.chap8;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Stack;

public class PalindromeLinkedList {

	public boolean isPalindrome1(ListNode head) {
		final var stack = new Stack<Integer>();

		var node = head;

		final int i = 1;

		while (node != null) {
			stack.add(node.val);
			node = node.next;
		}

		while (head != null) {

			if (head.val != stack.pop()) {
				return false;
			}

			head = head.next;

		}

		return true;
	}

	public boolean isPalindrome2(ListNode head) {
		Deque<Integer> deque = new LinkedList<>();

		ListNode node = head;

		while (node != null) {
			deque.add(node.val);

			node = node.next;
		}

		while (!deque.isEmpty() && deque.size() > 1) {
			if (!Objects.equals(deque.pollFirst(), deque.pollLast())) {
				return false;
			}
		}

		return true;
	}

	public static class ListNode {
		private int val;
		private ListNode next;

		ListNode() {
		}

		ListNode(int val) {
			this.val = val;
		}

		ListNode(int val, ListNode next) {
			this.val = val;
			this.next = next;
		}

		public int getVal() {
			return val;
		}

		public ListNode getNext() {
			return next;
		}
	}
}
