package org.example.chap8;

public class ReverseLinkedList {


	public ListNode reverseList(ListNode head) {
		return reverse(head, null);
	}

	public ListNode reverse(ListNode node, ListNode prev) {
		if (node == null) {
			return prev;
		}

		ListNode next = node.next;

		node.next = prev;

		return reverse(next, node);
	}

	public ListNode reverseList2(ListNode head) {

		ListNode prev = null, node = head;

		// 1 2 3 4
		while (node != null) {

			ListNode next = node.next;

			node.next = prev;

			prev = node;

			node = next;
		}

		return prev;
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
