-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 09, 2025 at 10:03 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dbprojecteva`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbllessons`
--

CREATE TABLE `tbllessons` (
  `lesson_id` int(11) NOT NULL,
  `topic_title` varchar(255) NOT NULL,
  `lesson_text` text NOT NULL,
  `code_snippet` text DEFAULT NULL,
  `visualizer_path` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbllessons`
--

INSERT INTO `tbllessons` (`lesson_id`, `topic_title`, `lesson_text`, `code_snippet`, `visualizer_path`, `created_at`, `updated_at`) VALUES
(1, 'Arrays', 'Welcome back, adventurer. You now traverse the Arrayed Highlands — the foundational terrain of the Kingdom of EVA. Here, you’ll master one of the most essential tools in your arsenal: the Array.\n\nAn array is a collection of elements, each identified by an index or position. All elements stored in an array must be of the same data type. Arrays allow for efficient access to data, making them ideal for scenarios where you need to store multiple values in a structured and predictable way.\n\nThink of an array as a row of chests, each labeled with a number (starting from 0). You can store an item (value) in each chest, and you can retrieve any item instantly by using its label.', 'int[] numbers = new int[5];\nnumbers[0] = 10;\nnumbers[1] = 20;\nnumbers[2] = 30;\nSystem.out.println(numbers[1]); // Output: 20', 'array-visualizer-view.fxml', '2025-05-04 04:39:20', '2025-05-04 04:39:20'),
(2, 'Linked Lists', 'You now tread the Path of Nodes, a winding route across the Kingdom of EVA known as the Linked List.\n\nA linked list is a linear data structure in which elements (called nodes) are connected using pointers. Each node contains two parts: the data and a reference to the next node in the sequence.\n\nUnlike arrays, linked lists do not require contiguous memory. This allows for efficient insertions and deletions but comes at the cost of slower access times.\n\nIn EVA, the path of the courier network is linked node by node — each message is passed from one post to the next until it reaches its destination.', 'class Node {\n    int data;\n    Node next;\n\n    Node(int data) {\n        this.data = data;\n        this.next = null;\n    }\n}\n\nNode head = new Node(10);\nhead.next = new Node(20);\nhead.next.next = new Node(30);', 'linkedlist-visualizer-view.fxml', '2025-05-04 07:29:31', '2025-05-09 20:02:58'),
(3, 'Stacks', 'You now arrive at the Tower of Stacks, where structure and order rule. A stack is a linear data structure that follows the Last-In-First-Out (LIFO) principle — the last element added is the first one removed.\n\nIn the real world, think of a stack as a pile of books or plates. You always remove the topmost one first. Stacks are widely used in function calls, undo operations, and syntax parsing.\n\nWithin the Kingdom of EVA, the stack is your spell scroll pile — what you add last to your spellbook is what you will cast first.', 'Stack<Integer> stack = new Stack<>();\nstack.push(10);\nstack.push(20);\nstack.pop(); // Removes 20\nSystem.out.println(stack.peek()); // Output: 10', 'stack-visualizer-view.fxml', '2025-05-04 04:39:20', '2025-05-09 20:02:52'),
(4, 'Queues', 'Welcome to the Courtyard of Queues, where order is maintained with discipline and patience. A queue is a linear data structure that follows the First-In-First-Out (FIFO) principle — the first element added is the first to be removed.\n\nYou can imagine a queue as a line of adventurers waiting their turn at the kingdom’s blacksmith. The one who arrives first is served first. Queues are crucial in scheduling, buffering, and task management.\n\nIn EVA, queues keep the kingdom running smoothly — from message delivery to handling player requests.', 'Queue<String> queue = new LinkedList<>();\nqueue.add(\"Elyse\");\nqueue.add(\"Darius\");\nqueue.remove(); // Removes Elyse\nSystem.out.println(queue.peek()); // Output: Darius', 'queue-visualizer-view.fxml', '2025-05-04 04:39:20', '2025-05-09 20:02:33'),
(5, 'Deque', 'Welcome back, adventurer. You now stand at the Deque Dales — the dual-gated outpost of the Kingdom of EVA. Here, you’ll master a powerful extension of your queue-and-stack arsenal: the Deque, or Double-Ended Queue.\r\n\r\nA deque is a collection of elements that supports insertion and removal at both ends. Like a queue, it can enqueue at the back and dequeue from the front; like a stack, it can push and pop at the front. This versatility makes deques ideal for problems requiring sliding windows, undo-redo histories, or any scenario where you need efficient access to both the newest and oldest items.\r\n\r\nThink of a deque as a long hallway of chests with two doors—one at each end. You can slip treasures through the front door or the back door, and you can retrieve them just as swiftly from either entrance. Whether you need last-in-first-out agility or first-in-first-out order, the Deque Dales bend to your command.', 'Deque<Integer> dq = new ArrayDeque<>();\r\n\r\n        dq.addLast(10);\r\n        dq.addLast(20);\r\n        dq.addFirst(30);\r\n        // Pop from front and back\r\n        dq.removeFirst(); \r\n        dq.removeLast();\r\n\r\n        // Print deque elements after pop\r\n        for (int x : dq) System.out.print(x + \" \");\r\n\r\n//Output: 20', 'deque-visualizer-view.fxml', '2025-05-09 19:57:24', '2025-05-09 20:01:50'),
(6, 'Hashtables', 'At the Archives of Mapping within EVA, data is retrieved with precision — welcome to the world of Hashtables.\n\nA hashtable is a data structure that maps keys to values using a hash function. This function computes an index where the value is stored. Ideally, it offers constant-time performance for lookups, insertions, and deletions.\n\nCollisions may occur, and different strategies (like chaining or open addressing) are used to handle them. Hashtables are ideal for dictionary-like operations.\n\nIn the Kingdom, this is the Royal Registry — fast, organized, and always one lookup away.', 'Map<String, Integer> map = new HashMap<>();\nmap.put(\"Gold\", 100);\nmap.put(\"Silver\", 50);\nSystem.out.println(map.get(\"Gold\")); // Output: 100', 'hashtable-visualizer-view.fxml', '2025-05-04 07:29:31', '2025-05-09 20:01:54'),
(7, 'Binary Search Trees', 'You stand at the Forking Forest, where the Binary Search Tree (BST) guides the flow of data. A BST is a node-based tree structure in which each node has at most two children: left and right.\n\nThe key property is that for every node, all elements in its left subtree are less than the node, and all elements in the right subtree are greater. This makes searching, inserting, and deleting efficient — on average O(log n).\n\nIn EVA, this structure forms the foundation of the kingdom’s decision system — logical, ordered, and recursive.', 'class Node {\n    int data;\n    Node left, right;\n\n    Node(int value) {\n        data = value;\n        left = right = null;\n    }\n}\n\n// Insert example:\nNode root = new Node(50);\nroot.left = new Node(30);\nroot.right = new Node(70);', 'bst-visualizer-view.fxml', '2025-05-04 07:29:31', '2025-05-09 19:59:58'),
(8, 'AVL Trees', 'Welcome to the Balancer’s Grove, where equilibrium is law. The AVL Tree is a self-balancing Binary Search Tree.\n\nAfter each insertion or deletion, the tree performs rotations to ensure that the difference in height (balance factor) between the left and right subtrees of any node is no more than 1. This guarantees O(log n) time complexity for operations.\n\nIn the Kingdom of EVA, the AVL Tree serves as the mechanism that prevents chaos in the data forests — maintaining balance at all costs.', 'class AVLNode {\n    int key, height;\n    AVLNode left, right;\n\n    AVLNode(int d) {\n        key = d;\n        height = 1;\n    }\n}\n\n// Rotations and balancing logic are applied during insertion.', 'avltree-visualizer-view.fxml', '2025-05-04 07:29:31', '2025-05-09 19:59:36'),
(9, 'Splay Trees', 'Beyond the Grove, the trees shift restlessly — welcome to the Domain of the Splay Tree.\n\nA Splay Tree is a self-adjusting binary search tree. When a node is accessed, it is moved to the root using a sequence of tree rotations (zig, zig-zig, zig-zag). Frequently accessed nodes are quicker to reach.\n\nThough its worst-case operations are O(n), amortized performance is O(log n), making it efficient over time for non-uniform access patterns.\n\nIn EVA, the Splay Tree is the rogue’s structure — adapting swiftly to whoever knocks on its branches.', 'class Node {\n    int key;\n    Node left, right;\n\n    Node(int item) {\n        key = item;\n        left = right = null;\n    }\n}\n\n// Splaying occurs after insertion, deletion, or search.', 'splaytree-visualizer-view.fxml', '2025-05-04 07:29:31', '2025-05-09 19:59:01');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbllessons`
--
ALTER TABLE `tbllessons`
  ADD PRIMARY KEY (`lesson_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbllessons`
--
ALTER TABLE `tbllessons`
  MODIFY `lesson_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
