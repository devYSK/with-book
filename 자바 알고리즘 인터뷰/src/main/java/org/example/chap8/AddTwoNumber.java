package org.example.chap8;

import java.math.BigInteger;

public class AddTwoNumber {

	public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

		final BigInteger bigInteger = toBigInt(reverse(l1)).add(toBigInt(reverse(l2)));

		return toListNode(bigInteger);
	}

	public ListNode toListNode(BigInteger bigInteger) {
		String str = bigInteger.toString();
		ListNode dummy = new ListNode(-1);
		ListNode curr = dummy;

		for (int i = str.length() - 1; i >= 0; i--) {

			curr.next = new ListNode(str.charAt(i) - '0');
			curr = curr.next;
		}

		return dummy.next;
	}

	public ListNode reverse(ListNode head) {
		ListNode prev = null;
		ListNode node = head;

		while (node != null) {
			ListNode next = node.next;
			node.next = prev;
			prev = node;
			node = next;
		}

		return prev;
	}

	public BigInteger toBigInt(ListNode node) {
		String value = "";

		while (node != null) {
			value += node.val;
			node = node.next;
		}

		return new BigInteger(value);
	}

}
