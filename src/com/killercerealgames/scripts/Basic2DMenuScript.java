package com.killercerealgames.scripts;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.IBaseItem;
import com.uwsoft.editor.renderer.script.IScript;

/*
 * Created by billzo on 10/16/2014
 * Feel free to email me at billzo@killercerealgames.com
 * This script is to be a larger part of a collection of scripts for Overlap2D
 * Overlap2D can be found here at overlap2d.com
 */

/* Requirements:
 * 	1) A composite item with number of columns and rows (think counting numbers, not array numbers) that will act as the menu
 * 	*) Optional: specify the starting default row and column (but can be changed at any time)
 * 	2) MenuItems that are defined as either (without quotes!):
 * 		i) "selector" - the item that does the selecting
 * 		ii) "selectable" - the items that can be selected
 * 	3) Every "selectable" item must specify its location in custom variables (without quotes!):
 * 		i) "row" - the row it is in
 * 		ii) "column" - the column it is in
 * 		NOTE: While you can have uneven rows and columns at their end, you CANNOT skip a number in the middle
 * 			That is to say, you can have a row of 3 and a row of 4, but each number from 1 - 3 and 1 - 4 must be occupied by an item
 * 	4) The "selectable" items have only been tested on CompositeItems (including buttons!) and imageItems!!
 */

/* Notice!:
 * The numbers in here contain both counting numbers of rows and columns as well as array numbers
 * If you do not want to change the this script internally and only want to pass numbers in,
 * Then please use the counting number (i.e. row 1, column 1) if manually setting position
 * *NOT* array[0][0] for that same spot!
 * Otherwise, you'll have to deduce which is appropriate given the context of its use herein
 */


public class Basic2DMenuScript implements IScript {

	private CompositeItem menu;
	private IBaseItem[][] menuItems;
	
	private Actor selector;
	private IBaseItem itemCurrentlySelected;
	private Actor actorCurrentlySelected;
	
	private int rowCurrentlySelected;
	private int columnCurrentlySelected;
	
	private int numberOfRows;
	private int numberOfColumns;
	
	private Integer rowDefault;
	private Integer columnDefault;
	
	/**
	 * Constructs an instance of the Script.
	 * You must construct an instance of the script for each Basic2DMenu you wish to have.
	 */
	public Basic2DMenuScript() {}
	
	/**
	 * Initializes the script given the CompositeItem to act as the menu.
	 * It collects all the items with customVarible "selectable" and the item
	 * that has customVariable "selector".
	 * It is optional to provide a customVariable of "defaultRow" and "defaultColumn"
	 * but this can also be set during runtime.
	 * Rows and columns can be of different lengths AT THE ENDS but each spot WITHIN *MUST* have an item
	 */
	public void init(CompositeItem menu) {
		this.menu = menu;
		
		numberOfRows = menu.getCustomVariables().getIntegerVariable("rows");
		numberOfColumns = menu.getCustomVariables().getIntegerVariable("columns");
		
		menuItems  = new IBaseItem[numberOfRows][numberOfColumns];
		
		rowDefault = menu.getCustomVariables().getIntegerVariable("defaultRow");
		columnDefault = menu.getCustomVariables().getIntegerVariable("defaultColumn");
		
		if (rowDefault == null) rowDefault = 0;
		if (columnDefault == null) rowDefault = 0;
		
		for (IBaseItem item : menu.getItems()) {
			
			if (item.getCustomVariables().getStringVariable("menuItemType") != null) {
				
				if (item.getCustomVariables().getStringVariable("menuItemType").equals("selectable")) {
					int rowNumber = item.getCustomVariables().getIntegerVariable("row");
					int columnNumber = item.getCustomVariables().getIntegerVariable("column");
					menuItems[rowNumber - 1][columnNumber - 1] = item;
				}
				else if (item.getCustomVariables().getStringVariable("menuItemType").equals("selector")) {
					selector = (Actor) item;
				}
			}
			
		}
		
		selector.setVisible(false);
		
		setVisible(true);
		
		setAtDefaultMenuPosition();
		
	}
	
	/**
	 * Sets "selector" default row
	 * @param row
	 */
	public void setDefaultRow(int row) {
		rowDefault = row;
	}
	
	/**
	 * Sets "selector" default column
	 * @param column
	 */
	public void setDefaultColumn(int column) {
		columnDefault = column;
	}
	
	/**
	 * Sets "selector" default row and column
	 * @param row
	 * @param column
	 */
	public void setDefaultPosition(int row, int column) {
		rowDefault = row;
		columnDefault = column;
	}
	
	/**
	 * Sets the "selector" back to the described default positions
	 */
	public void setAtDefaultMenuPosition() {
		rowCurrentlySelected = columnDefault - 1;
		columnCurrentlySelected = rowDefault - 1;
		updateCurrentlySelectedItem();
		setSelectorPositionToCurrentlySelectedActor();
		selector.setVisible(true);
	}
	
	/**
	 * Sets the menu visibility
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		menu.setVisible(visible);
	}
	
	/**
	 * Returns the instance of the Actor that is acting as the "selector"
	 */
	public Actor getMenuSelector() {
		return selector;
	}
	
	/**
	 * Decreases the currently selected row by one row.
	 * Does a null check automatically and and will never reach zero.
	 */
	public void decreaseRowNumber() {
		if (rowCurrentlySelected == 0) {
			return;
		}
		else {
			rowCurrentlySelected--;
			updateCurrentlySelectedItem();
			if (nullCheck()) {
				rowCurrentlySelected++;
				updateCurrentlySelectedItem();
				return;
			}
			setSelectorPositionToCurrentlySelectedActor();
		}
	}
	
	/**
	 * Increases the currently selected row by one row.
	 * Does a null check automatically and and will never reach zero.
	 */
	public void increaseRowNumber() {
		if (rowCurrentlySelected >= numberOfRows - 1) {
			rowCurrentlySelected = numberOfRows - 1;
		}
		else {
			rowCurrentlySelected++;
			updateCurrentlySelectedItem();
			if (nullCheck()) {
				rowCurrentlySelected--;
				updateCurrentlySelectedItem();
				return;
			}
			setSelectorPositionToCurrentlySelectedActor();
		}
	}
	
	/**
	 * Decreases the currently selected column by one row.
	 * Does a null check automatically and and will never reach zero.
	 */
	public void decreaseColumnNumber() {
		if (columnCurrentlySelected == 0) {
			return;
		}
		else {
			columnCurrentlySelected--;
			updateCurrentlySelectedItem();
			if (nullCheck()) {
				columnCurrentlySelected++;
				updateCurrentlySelectedItem();
				return;
			}
			setSelectorPositionToCurrentlySelectedActor();
		}
	}
	
	/**
	 * Increases the currently selected column by one row.
	 * Does a null check automatically and and will never reach zero.
	 */
	public void increaseColumnNumber() {
		if (columnCurrentlySelected == numberOfColumns - 1) {
			return;
		}
		else {
			columnCurrentlySelected++;
			updateCurrentlySelectedItem();
			if (nullCheck()) {
				columnCurrentlySelected--;
				updateCurrentlySelectedItem();
				return;
			}
			setSelectorPositionToCurrentlySelectedActor();
		}
	}
	
	/** 
	 * Sets the position manually by row and column. This does not do a null check automatically.
	 * Be sure an item actually exists at the target position.
	 * @param row
	 * @param column
	 */
	public void setPosition(int row, int column) {
		rowCurrentlySelected = row - 1;
		columnCurrentlySelected = column - 1;
		updateCurrentlySelectedItem();
		setSelectorPositionToCurrentlySelectedActor();
	}
	
	/**
	 * Returns an instance of the IBaseItem that is currently selected
	 * @return
	 */
	public IBaseItem getCurrentItemSelected() {
		itemCurrentlySelected = menuItems[rowCurrentlySelected][columnCurrentlySelected];
		return itemCurrentlySelected;
	}
	
	/**
	 * Returns an instance of the Actor that is currently selected
	 * @return
	 */
	public Actor getCurrentActorSelected() {
		actorCurrentlySelected = (Actor) menuItems[rowCurrentlySelected][columnCurrentlySelected];
		return actorCurrentlySelected;
	}
	
	/**
	 * This is where you would edit the script in order to change how the selector is visually selecting.
	 */
	// TODO: Please customize how to set the "selectors" position given the selected item
	private void setSelectorPositionToCurrentlySelectedActor() {
		actorCurrentlySelected = (Actor) itemCurrentlySelected;
		selector.setPosition(actorCurrentlySelected.getCenterX() - Math.abs((selector.getWidth() - actorCurrentlySelected.getWidth()) / 2),
				actorCurrentlySelected.getCenterY() - Math.abs((selector.getHeight() - actorCurrentlySelected.getHeight()) / 2));
	}
	
	// Acting takes place in the setSelectorPositionToCurrentlySelectedActor method in that regard.
	// However, it is also possible to have a button, etc. within this context as well.
	@Override
	public void act(float delta) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Updates the currently selected actor. This does not do a null check automatically by itself.
	 */
	private void updateCurrentlySelectedItem() {
		itemCurrentlySelected = menuItems[rowCurrentlySelected][columnCurrentlySelected];
	}
	
	/**
	 * Does the null checking for a currently selected actor. This should be called AFTER updating the
	 * currently selected actor but BEFORE setting the position of the selector
	 * @return
	 */
	private boolean nullCheck() {
		if (itemCurrentlySelected == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void dispose() {}

	
	
}
