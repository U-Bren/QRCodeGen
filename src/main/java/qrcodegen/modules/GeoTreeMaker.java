/*
 * Copyright (C) 2013 Stefan Ganzer
 *
 * This file is part of QRCodeGen.
 *
 * QRCodeGen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QRCodeGen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package qrcodegen.modules;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import qrcodegen.kml.Document;
import qrcodegen.kml.Folder;
import qrcodegen.kml.Kml;
import qrcodegen.kml.Placemark;

/**
 *
 * @author Stefan Ganzer
 */
public class GeoTreeMaker {

	private final Kml data;
	private DefaultMutableTreeNode tree;

	public GeoTreeMaker(Kml data) {
		if (data == null) {
			throw new NullPointerException();
		}
		this.data = data;
	}

	public void generateTree(boolean createNodesForDocuments) {
		tree = new DefaultMutableTreeNode(data);

		addNodeFor(data.getPlacemark(), tree);
		addNodeFor(data.getFolder(), tree, createNodesForDocuments);
		addNodeFor(data.getDocument(), tree, createNodesForDocuments);
	}

	public void generateTree() {
		generateTree(true);
	}

	private void addNodeFor(Folder f, DefaultMutableTreeNode root, boolean createNodesForDocuments) {
		if (f == null) {
			return;
		}
		DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(f);
		addNodeForPlacemarks(f.getPlacemarks(), folderNode);
		addNodeForFolders(f.getFolders(), folderNode, createNodesForDocuments);
		addNodeForDocuments(f.getDocuments(), folderNode, createNodesForDocuments);
		root.add(folderNode);
	}

	private void addNodeFor(Placemark p, DefaultMutableTreeNode root) {
		if (p == null) {
			return;
		}
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(p);
		root.add(node);
	}

	private void addNodeFor(Document d, DefaultMutableTreeNode root, boolean createNodeForDocuments) {
		if (d == null) {
			return;
		}
		DefaultMutableTreeNode docNode = createNodeForDocuments ? new DefaultMutableTreeNode(d) : root;
		addNodeForPlacemarks(d.getPlacemarks(), docNode);
		addNodeForFolders(d.getFolders(), docNode, createNodeForDocuments);
		addNodeForDocuments(d.getDocuments(), docNode, createNodeForDocuments);
		if (createNodeForDocuments) {
			root.add(docNode);
		}
	}

//	private void addNodeFor(Document d, DefaultMutableTreeNode root) {
//		if (d == null) {
//			return;
//		}
//		addNodeForPlacemarks(d.getPlacemarks(), root);
//		addNodeForFolders(d.getFolders(), root);
//		addNodeForDocuments(d.getDocuments(), root);
//	}
	private void addNodeForFolders(List<Folder> folder, DefaultMutableTreeNode root, boolean createNodesForDocuments) {
		if (folder == null) {
			return;
		}
		for (Folder f : folder) {
			addNodeFor(f, root, createNodesForDocuments);
		}
	}

	private void addNodeForPlacemarks(List<Placemark> placemarks, DefaultMutableTreeNode root) {
		if (placemarks == null) {
			return;
		}
		for (Placemark p : placemarks) {
			addNodeFor(p, root);
		}
	}

	private void addNodeForDocuments(List<Document> document, DefaultMutableTreeNode root, boolean createNodeForDocuments) {
		if (document == null) {
			return;
		}
		for (Document d : document) {
			addNodeFor(d, root, createNodeForDocuments);
		}
	}

	public DefaultMutableTreeNode getTree() {
		return tree;
	}

	public DefaultTreeModel getTreeModel() {
		return new DefaultTreeModel(tree);
	}
}
