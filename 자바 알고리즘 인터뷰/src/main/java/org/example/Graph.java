package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

class Graph {
	private int numVertices;
	private LinkedList<Integer>[] adjacencyList;

	@SuppressWarnings("unchecked")
	Graph(int numVertices) {
		this.numVertices = numVertices;
		adjacencyList = new LinkedList[numVertices];
		for (int i = 0; i < numVertices; i++) {
			adjacencyList[i] = new LinkedList<>();
		}
	}

	void addEdge(int vertex, int connectedVertex) {
		adjacencyList[vertex].add(connectedVertex);
	}

	public void breadthFirstSearch(int startVertex) {
		boolean[] isVisited = new boolean[numVertices];
		Queue<Integer> queue = new LinkedList<>();

		isVisited[startVertex] = true;
		queue.add(startVertex);

		while (!queue.isEmpty()) {
			int currentVertex = queue.poll();
			System.out.print(currentVertex + " ");

			for (Integer neighbor : adjacencyList[currentVertex]) {
				if (!isVisited[neighbor]) {
					isVisited[neighbor] = true;
					queue.add(neighbor);
				}
			}
		}
	}

	public void depthFirstSearch(int startVertex) {
		boolean[] isVisited = new boolean[numVertices];
		Stack<Integer> stack = new Stack<>();

		stack.push(startVertex);

		while (!stack.isEmpty()) {
			int currentVertex = stack.pop();

			if (!isVisited[currentVertex]) {
				System.out.print(currentVertex + " ");
				isVisited[currentVertex] = true;
			}

			for (Integer neighbor : adjacencyList[currentVertex]) {
				if (!isVisited[neighbor]) {
					stack.push(neighbor);
				}
			}
		}
	}

	public static void main(String args[]) {
		Graph g = new Graph(4);

		g.addEdge(0, 1);
		g.addEdge(0, 2);
		g.addEdge(1, 2);
		g.addEdge(2, 0);
		g.addEdge(2, 3);
		g.addEdge(3, 3);

		System.out.print("BFS Traversal from vertex 2: ");
		// g.BFS(2);

		System.out.println(); // New line for clarity

		System.out.print("DFS Traversal from vertex 2: ");
		// g.DFS(2);
	}
}