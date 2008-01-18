/*
 * ProgressTreePanel.java
 *
 * Created on October 28, 2007, 1:16 AM
 */

package org.jmist.framework.reporting;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * A <code>JPanel</code> that allows the user to navigate a tree of progress
 * indicators.
 * @author  bkimmel
 */
public class ProgressTreePanel extends javax.swing.JPanel implements ProgressMonitor {

	/** Creates new form ProgressTreePanel */
	public ProgressTreePanel() {
		this.top = new ProgressNode("");
		initComponents();
		this.refresh();
	}

	/**
	 * Creates a new <code>ProgressTreePanel</code>.
	 * @param title The title of the root task.
	 */
	public ProgressTreePanel(String title) {
		this.top = new ProgressNode(title);
		initComponents();
		this.refresh();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		javax.swing.JPanel progressNodeComponent;

		parentButton = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		childrenTable = new javax.swing.JTable();
		progressNodePanel = new javax.swing.JPanel();
		progressNodeComponent = getTopNodeComponent();
		rootButton = new javax.swing.JButton();

		parentButton.setText("<<");
		parentButton.setEnabled(false);
		parentButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				parentButtonActionPerformed(evt);
			}
		});

		childrenTable.setModel(getTableModel());
		childrenTable.getColumn("Progress").setCellRenderer(ProgressTableCellRenderer.getInstance());
		childrenTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				childrenTableMouseClicked(evt);
			}
		});

		jScrollPane1.setViewportView(childrenTable);

		javax.swing.GroupLayout progressNodeComponentLayout = new javax.swing.GroupLayout(progressNodeComponent);
		progressNodeComponent.setLayout(progressNodeComponentLayout);
		progressNodeComponentLayout.setHorizontalGroup(
			progressNodeComponentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGap(0, 408, Short.MAX_VALUE)
		);
		progressNodeComponentLayout.setVerticalGroup(
			progressNodeComponentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGap(0, 59, Short.MAX_VALUE)
		);

		javax.swing.GroupLayout progressNodePanelLayout = new javax.swing.GroupLayout(progressNodePanel);
		progressNodePanel.setLayout(progressNodePanelLayout);
		progressNodePanelLayout.setHorizontalGroup(
			progressNodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addComponent(progressNodeComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		progressNodePanelLayout.setVerticalGroup(
			progressNodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addComponent(progressNodeComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);

		rootButton.setText("O");
		rootButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				rootButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
					.addComponent(progressNodePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(layout.createSequentialGroup()
						.addComponent(rootButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(parentButton))
					.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
				.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(progressNodePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
					.addComponent(parentButton)
					.addComponent(rootButton))
				.addContainerGap())
		);
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * Fires when the user clicks on the table showing the current node's
	 * child progress indicators.
	 * @param evt The event arguments.
	 */
	private void childrenTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_childrenTableMouseClicked

		if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1 && evt.getClickCount() == 2) {

			int selectedRow = this.childrenTable.getSelectedRow();

			if (selectedRow >= 0) {
				evt.consume();
				this.moveToChild(selectedRow);
			}

		}

	}//GEN-LAST:event_childrenTableMouseClicked

	/**
	 * Fires when the user clicks on the button to switch to the root
	 * <code>ProgressMonitor</code>.
	 * @param evt The event arguments.
	 */
	private void rootButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootButtonActionPerformed
		this.moveToRoot();
	}//GEN-LAST:event_rootButtonActionPerformed

	/**
	 * Fires when the user clicks on the button to switch to the parent
	 * <code>ProgressMonitor</code>.
	 * @param evt The event arguments.
	 */
	private void parentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parentButtonActionPerformed
		this.moveToParent();
	}//GEN-LAST:event_parentButtonActionPerformed

	/**
	 * A <code>TableCellRenderer</code> for rendering progress bars in a table
	 * cell.
	 * @author bkimmel
	 */
	private static class ProgressTableCellRenderer implements TableCellRenderer {

		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			return (JProgressBar) value;

		}

		/**
		 * Creates a new <code>ProgressTableCellRenderer</code>.  This
		 * constructor is private because this class is a singleton.
		 */
		private ProgressTableCellRenderer() {
			// nothing to do.
		}

		/**
		 * Gets the single instance of <code>ProgressTableCellRenderer</code>.
		 * @return The single instance of <code>ProgressTableCellRenderer</code>.
		 */
		public static ProgressTableCellRenderer getInstance() {

			if (instance == null) {
				instance = new ProgressTableCellRenderer();
			}

			return instance;

		}

		/**
		 * The single instance of <code>ProgressTableCellRenderer</code>.
		 */
		private static ProgressTableCellRenderer instance = null;

	}

	/**
	 * Gets the <code>JPanel</code> displaying the current
	 * <code>ProgressMonitor</code>.
	 * @return The <code>JPanel</code> displaying the current
	 * 		<code>ProgressMonitor</code>.
	 */
	private JPanel getTopNodeComponent() {
		return (JPanel) this.top.getComponent();
	}

	/**
	 * Gets the <code>TableModel</code> for the current
	 * <code>ProgressMonitor</code>s children.
	 * @return The <code>TableModel</code> for the current
	 * 		<code>ProgressMonitor</code>s children.
	 */
	private TableModel getTableModel() {
		return this.top;
	}

	/**
	 * Redisplays the contents of this <code>ProgressTreePanel</code>.
	 */
	private void refresh() {

		/* Update the top level progress indicator. */
		JComponent progressNodeComponent = this.getTopNodeComponent();

		this.progressNodePanel.removeAll();

		javax.swing.GroupLayout progressNodePanelLayout = new javax.swing.GroupLayout(progressNodePanel);
		progressNodePanel.setLayout(progressNodePanelLayout);
		progressNodePanelLayout.setHorizontalGroup(
			progressNodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addComponent(progressNodeComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		progressNodePanelLayout.setVerticalGroup(
			progressNodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addComponent(progressNodeComponent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);

		/* Update the child table. */
		this.childrenTable.setModel(this.getTableModel());
		this.childrenTable.getColumn("Progress").setCellRenderer(ProgressTableCellRenderer.getInstance());

		this.parentButton.setEnabled(this.top.getParent() != null);

	}

	/**
	 * Gets the root <code>ProgressNode</code>.
	 * @return The root <code>ProgressNode</code>.
	 */
	private ProgressNode getRootNode() {

		ProgressNode node = this.top;

		while (node.getParent() != null) {
			node = node.getParent();
		}

		return node;

	}

	/**
	 * Sets the specified <code>ProgressNode</code> as the active node.  This
	 * will put that node at the top of the <code>JPanel</code> and the table
	 * will be populated with it's children.
	 * @param node The <code>ProgressNode</code> to make the active node.
	 */
	private void moveToNode(ProgressNode node) {
		this.top = node;
		this.refresh();
	}

	/**
	 * Makes the root node active.
	 */
	private void moveToRoot() {
		this.moveToNode(this.getRootNode());
	}

	/**
	 * Makes the active nodes parent node the active node.  This method assumes
	 * that the current node is not the root node.
	 */
	private void moveToParent() {

		ProgressNode parent = this.top.getParent();
		assert(parent != null);

		this.moveToNode(parent);

	}

	/**
	 * Makes one of the active node's children the active node.
	 * @param index The index of the child to make active.
	 */
	private void moveToChild(int index) {
		this.moveToNode(this.top.getChild(index));
	}

	/**
	 * The <code>ProgressMonitor</code> used by <code>ProgressTreePanel</code>
	 * and the <code>TableModel</code> used by <code>childrenTable</code>.
	 * @author bkimmel
	 */
	private static final class ProgressNode extends AbstractTableModel implements ProgressMonitor {

		/**
		 * Creates the root <code>ProgressNode</code>.
		 * @param title The title of the node.
		 */
		public ProgressNode(String title) {
			this.title = title;
			this.parent = null;
		}

		/**
		 * Creates a child <code>ProgressNode</code>.
		 * @param title The title of the node.
		 * @param parent The parent <code>ProgressNode</code>.
		 */
		private ProgressNode(String title, ProgressNode parent) {
			this.title = title;
			this.parent = parent;
		}

		/* (non-Javadoc)
		 * @see org.jmist.framework.reporting.ProgressMonitor#isCancelPending()
		 */
		@Override
		public boolean isCancelPending() {
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see org.jmist.framework.reporting.ProgressMonitor#notifyProgress(double)
		 */
		@Override
		public boolean notifyProgress(double progress) {
			this.progressBar.setStringPainted(false);
			this.setProgressBarValue((int) Math.floor(progress * 100.0), 100);
			return !this.isCancelPending();
		}

		/* (non-Javadoc)
		 * @see org.jmist.framework.reporting.ProgressMonitor#notifyProgress(int, int)
		 */
		@Override
		public boolean notifyProgress(int value, int maximum) {
			this.progressBar.setString(String.format("(%d/%d)", value, maximum));
			this.progressBar.setStringPainted(true);
			this.setProgressBarValue(value, maximum);
			return !this.isCancelPending();
		}

		/**
		 * Updates the progress bar.
		 * @param value The value of the progress bar.
		 * @param maximum The maximum value of the progress bar.
		 */
		private void setProgressBarValue(int value, int maximum) {

			this.progressBar.setIndeterminate(false);

			if (this.progressBar.getMaximum() != maximum) {
				this.progressBar.setMaximum(maximum);
			}

			this.progressBar.setValue(value);
			this.fireColumnChanged(PROGRESS_COLUMN);

		}

		/* (non-Javadoc)
		 * @see org.jmist.framework.reporting.ProgressMonitor#notifyIndeterminantProgress()
		 */
		@Override
		public boolean notifyIndeterminantProgress() {
			this.progressBar.setIndeterminate(true);
			this.fireColumnChanged(PROGRESS_COLUMN);
			return !this.isCancelPending();
		}

		/* (non-Javadoc)
		 * @see org.jmist.framework.reporting.ProgressMonitor#notifyStatusChanged(java.lang.String)
		 */
		@Override
		public void notifyStatusChanged(String status) {

			this.status = status;

			if (this.statusLabel != null) {
				this.statusLabel.setText(this.status);
			}

			if (this.parent != null) {
				this.fireColumnChanged(STATUS_COLUMN);
			}

		}

		/* (non-Javadoc)
		 * @see org.jmist.framework.reporting.ProgressMonitor#notifyCancelled()
		 */
		@Override
		public void notifyCancelled() {
			this.removeFromParent();
		}

		/* (non-Javadoc)
		 * @see org.jmist.framework.reporting.ProgressMonitor#notifyComplete()
		 */
		@Override
		public void notifyComplete() {
			this.removeFromParent();
		}

		/**
		 * Removes this <code>ProgressNode</code> from its parent if this node
		 * is not the root node.
		 */
		private void removeFromParent() {
			if (this.parent != null) {
				this.parent.removeChild(this);
			}
		}

		/* (non-Javadoc)
		 * @see org.jmist.framework.reporting.ProgressMonitor#createChildProgressMonitor(java.lang.String)
		 */
		@Override
		public ProgressNode createChildProgressMonitor(String title) {
			ProgressNode node = new ProgressNode(title, this);
			int index = this.children.size();
			this.children.add(node);
			this.fireTableRowsInserted(index, index);
			return node;
		}

		/**
		 * Gets this <code>ProgressNode</code>'s parent.
		 * @return The parent of this <code>ProgressNode</code>.
		 */
		public ProgressNode getParent() {
			return this.parent;
		}

		/**
		 * Gets the number of children of this <code>ProgressNode</code>.
		 * @return The number of children of this <code>ProgressNode</code>.
		 */
		public int getNumChildren() {
			return this.children.size();
		}

		/**
		 * Gets a child of this <code>ProgressNode</code>.
		 * @param index The index into the list of children of the child to
		 * 		get.
		 * @return The child <code>ProgressNode</code> at the specified index.
		 */
		public ProgressNode getChild(int index) {
			return this.children.get(index);
		}

		/**
		 * Removes the specified child from this <code>ProgressNode</code>.
		 * @param child The child <code>ProgressNode</code> to remove.
		 */
		public synchronized void removeChild(ProgressNode child) {
			int index = this.children.indexOf(child);
			this.children.remove(index);
			this.fireTableRowsDeleted(index, index);
		}

		/**
		 * Gets the index of the specified child node.
		 * @param child The child <code>ProgressNode</code> to find.
		 * @return The index of the <code>child</code>, or <code>-1</code> if
		 * 		there is no such child node.
		 */
		private synchronized int indexOf(ProgressNode child) {
			return this.children.indexOf(child);
		}

		/**
		 * Fires an event on the parent <code>ProgressNode</code> notifying
		 * its listeners that the specified column has changed.
		 * @param column The index of the column that changed.
		 */
		private void fireColumnChanged(int column) {
			if (this.parent != null) {
				this.parent.fireTableCellUpdated(this.parent.indexOf(this), column);
			}
		}

		/**
		 * Gets the stand-alone <code>JComponent</code> to display when this
		 * <code>ProgressNode</code> is the active node.
		 * @return The stand-alone <code>JComponent</code> to display when this
		 * 		<code>ProgressNode</code> is the active node.
		 */
		public JComponent getComponent() {

			javax.swing.JPanel progressNodePanel;
			javax.swing.JLabel titleLabel;

			progressNodePanel = new javax.swing.JPanel();
			titleLabel = new javax.swing.JLabel();

			if (this.statusLabel == null) {
				this.statusLabel = new javax.swing.JLabel();
			}

			titleLabel.setText(this.title);
			this.statusLabel.setText(this.status);

			javax.swing.JPanel progressBarPanel = new javax.swing.JPanel();

			javax.swing.GroupLayout progressBarPanelLayout = new javax.swing.GroupLayout(progressBarPanel);
			progressBarPanel.setLayout(progressBarPanelLayout);
			progressBarPanelLayout.setHorizontalGroup(
				progressBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 408, Short.MAX_VALUE)
				.addComponent(this.progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
			);
			progressBarPanelLayout.setVerticalGroup(
				progressBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 19, Short.MAX_VALUE)
				.addComponent(this.progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
			);

			javax.swing.GroupLayout progressNodePanelLayout = new javax.swing.GroupLayout(progressNodePanel);
			progressNodePanel.setLayout(progressNodePanelLayout);
			progressNodePanelLayout.setHorizontalGroup(
				progressNodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
				.addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
				.addComponent(progressBarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			);
			progressNodePanelLayout.setVerticalGroup(
				progressNodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(progressNodePanelLayout.createSequentialGroup()
					.addComponent(titleLabel)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(statusLabel)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(progressBarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);

			return progressNodePanel;

		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			return NUM_COLUMNS;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			return this.children.size();
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public synchronized Object getValueAt(int rowIndex, int columnIndex) {

			if (rowIndex < this.children.size()) {

				ProgressNode node = this.children.get(rowIndex);

				switch (columnIndex) {

				case CHILDREN_COLUMN:
					return node.children.size();

				case TITLE_COLUMN:
					return node.title;

				case PROGRESS_COLUMN:
					return node.progressBar;

				case STATUS_COLUMN:
					return node.status;

				default:
					assert(false);
					return null;

				}

			} else {
				return null;
			}

		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return COLUMN_CLASS[columnIndex];
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int column) {
			return COLUMN_NAME[column];
		}

		/** The parent <code>ProgressNode</code> of this node. */
		private final ProgressNode			parent;

		/**
		 * The <code>List</code> of children of this <code>ProgressNode</code>.
		 */
		private final List<ProgressNode>	children			= new ArrayList<ProgressNode>();

		/**
		 * The progress bar that displays the progress for this
		 * <code>ProgressMonitor</code>.
		 */
		private final JProgressBar			progressBar			= new JProgressBar();

		/** The title of this <code>ProgressMonitor</code>. */
		private final String				title;

		/** The status text of this <code>ProgressMonitor</code>. */
		private String						status				= "";

		/**
		 * A <code>JLabel</code> displaying this <code>ProgressNode</code>'s
		 * status text.
		 */
		private javax.swing.JLabel			statusLabel			= null;

		/** The number of columns in this <code>TableModel</code>. */
		private static final int			NUM_COLUMNS			= 4;

		/** The index of the column displaying the number of children. */
		private static final int			CHILDREN_COLUMN		= 0;

		/** The index of the column displaying the title. */
		private static final int			TITLE_COLUMN		= 1;

		/** The index of the column displaying the progress bar. */
		private static final int			PROGRESS_COLUMN		= 2;

		/** The index of the column displaying the status text. */
		private static final int			STATUS_COLUMN		= 3;

		/** The classes of the columns. */
		private static final Class<?>[]		COLUMN_CLASS		= { Integer.class, String.class, JProgressBar.class, String.class };

		/** The names of the columns. */
		private static final String[]		COLUMN_NAME			= { "Children", "Title", "Progress", "Status" };

		/**
		 * Serialization version ID.
		 */
		private static final long serialVersionUID = 4409494195911210222L;

	}

	/* (non-Javadoc)
	 * @see org.jmist.framework.reporting.ProgressMonitor#createChildProgressMonitor(java.lang.String)
	 */
	@Override
	public ProgressMonitor createChildProgressMonitor(String title) {
		return this.getRootNode().createChildProgressMonitor(title);
	}

	/* (non-Javadoc)
	 * @see org.jmist.framework.reporting.ProgressMonitor#isCancelPending()
	 */
	@Override
	public boolean isCancelPending() {
		return this.getRootNode().isCancelPending();
	}

	/* (non-Javadoc)
	 * @see org.jmist.framework.reporting.ProgressMonitor#notifyCancelled()
	 */
	@Override
	public void notifyCancelled() {
		this.getRootNode().notifyCancelled();
	}

	/* (non-Javadoc)
	 * @see org.jmist.framework.reporting.ProgressMonitor#notifyComplete()
	 */
	@Override
	public void notifyComplete() {
		this.getRootNode().notifyComplete();
	}

	/* (non-Javadoc)
	 * @see org.jmist.framework.reporting.ProgressMonitor#notifyIndeterminantProgress()
	 */
	@Override
	public boolean notifyIndeterminantProgress() {
		return this.getRootNode().notifyIndeterminantProgress();
	}

	/* (non-Javadoc)
	 * @see org.jmist.framework.reporting.ProgressMonitor#notifyProgress(int, int)
	 */
	@Override
	public boolean notifyProgress(int value, int maximum) {
		return this.getRootNode().notifyProgress(value, maximum);
	}

	/* (non-Javadoc)
	 * @see org.jmist.framework.reporting.ProgressMonitor#notifyProgress(double)
	 */
	@Override
	public boolean notifyProgress(double progress) {
		return this.getRootNode().notifyProgress(progress);
	}

	/* (non-Javadoc)
	 * @see org.jmist.framework.reporting.ProgressMonitor#notifyStatusChanged(java.lang.String)
	 */
	@Override
	public void notifyStatusChanged(String status) {
		this.getRootNode().notifyStatusChanged(status);
	}

	/**
	 * The <code>ProgressNode</code> currently displaying at the top of this
	 * panel.
	 */
	private ProgressNode top;

	/**
	 * Serialization version ID.
	 */
	private static final long serialVersionUID = 4364840053111586849L;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JTable childrenTable;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JButton parentButton;
	private javax.swing.JPanel progressNodePanel;
	private javax.swing.JButton rootButton;
	// End of variables declaration//GEN-END:variables

}
