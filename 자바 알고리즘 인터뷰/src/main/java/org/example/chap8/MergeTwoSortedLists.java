package org.example.chap8;

public class MergeTwoSortedLists {

	public ListNode mergeTwoLists(ListNode list1, ListNode list2) {

		if (list1 == null) return list2;

		if (list2 == null) return list1;

		if (list1.val < list2.val) {
			list1.next = mergeTwoLists(list1.next, list2);
			return list1;
		} else {
			list2.next = mergeTwoLists(list1, list2.next);
			return list2;
		}
	}

	public ListNode mergeTwoLists2(ListNode list1, ListNode list2) {
		ListNode dummy = new ListNode(0); // 결과 연결 리스트의 더미 노드
		ListNode current = dummy; // 현재 노드 포인터

		while (list1 != null && list2 != null) {
			if (list1.val < list2.val) {
				current.next = list1;
				list1 = list1.next;
			} else {
				current.next = list2;
				list2 = list2.next;
			}
			current = current.next;
		}

		// list1이나 list2 중 하나가 아직 남아있다면 나머지를 추가
		if (list1 != null) {
			current.next = list1;
		} else {
			current.next = list2;
		}

		return dummy.next; // 더미 노드 다음 노드를 반환하여 병합된 리스트를 얻습니다.
	}

	public static class ListNode {
		int val;
		ListNode next;

		ListNode() {
		}

		ListNode(int val) {
			this.val = val;
		}

		ListNode(int val, ListNode next) {
			this.val = val;
			this.next = next;
		}
	}
}
