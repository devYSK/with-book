package org.example.chap8;

public class OddEventLinkedList {

	public ListNode oddEvenList(ListNode head) {

		if (head == null) {
			return null;
		}

		ListNode odd = head; // 홀수

		ListNode even = head.next;	 // 짝수

		ListNode evenHead = even;


		while (even != null && even.next != null) {
			odd.next = odd.next.next;
			even.next = even.next.next;

			odd = odd.next;
			even = even.next;
		}

		odd.next = evenHead;

		return head;
	}

}
