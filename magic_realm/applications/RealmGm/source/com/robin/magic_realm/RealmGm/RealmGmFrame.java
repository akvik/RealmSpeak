/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
package com.robin.magic_realm.RealmGm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileFilter;

import com.robin.game.objects.GameData;
import com.robin.general.io.FileUtilities;
import com.robin.general.io.PreferenceManager;
import com.robin.general.swing.ComponentTools;
import com.robin.general.swing.IconFactory;
import com.robin.magic_realm.RealmCharacterBuilder.RealmCharacterBuilderModel;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.swing.HostGameSetupDialog;
import com.robin.magic_realm.components.CharacterChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.GameFileFilters;
import com.robin.magic_realm.components.utility.RealmUtility;

public class RealmGmFrame extends JFrame {
	private static final String MetalLookAndFeel = "MLAF";
	private static final String TilesDisplayStyle = "TS";
	private static final String PreferredFilePath = "PFP";
	
	private static final String ChitDisplayStyle = "CDS";
	private static final String CharacterChitDisplayStyle = "CDS";
	
	private PreferenceManager prefs;
	private JDesktopPane desktop;
	private RealmGameEditor editor;
	
	private JMenuItem openGame;
	private JMenuItem closeGame;
	private JMenuItem saveGame;
	private JMenuItem saveAsGame;
	
	private JRadioButton classicChitsOption;
	private JRadioButton colorChitsOption;
	private JRadioButton frenzelChitsOption;
	private JRadioButton legendaryChitsOption;
	private JRadioButton classicCharacterChitsOption;
	private JRadioButton legendaryClassicCharacterChitsOption;
	private JRadioButton legendaryCharacterChitsOption;
	private JButton gameOptions;
	
	protected FileFilter saveGameFileFilter = GameFileFilters.createSaveGameFileFilter();
	
	public RealmGmFrame() {
		prefs = new PreferenceManager("RealmSpeak","RealmGm");
		prefs.loadPreferences();
		initComponents();
	}
	private void savePrefs() {
		prefs.savePreferences();
	}
	private void initComponents() {
		updateLookAndFeel();
		setTilesStyle();
		setChitDisplayStyle();
		setCharacterChitDisplayStyle();
		setTitle("RealmSpeak GM");
		setIconImage(IconFactory.findIcon("images/badges/elvish_studies.gif").getImage());
		setSize(1024,768);
		setLocationRelativeTo(null);
		
		setJMenuBar(buildMenuBar());
		setLayout(new BorderLayout());
		desktop = new JDesktopPane();
		add(desktop,BorderLayout.CENTER);
		
		updateControls();
	}
	public void updateControls() {
		openGame.setEnabled(editor==null);
		closeGame.setEnabled(editor!=null);
		saveGame.setEnabled(editor!=null && editor.getGameData().isModified());
		saveAsGame.setEnabled(editor!=null);
		gameOptions.setEnabled(editor!=null);
	}
	private void updateLookAndFeel() {
		if (prefs.getBoolean(MetalLookAndFeel)) {
			ComponentTools.setMetalLookAndFeel();
		}
		else {
			ComponentTools.setSystemLookAndFeel();
		}
		SwingUtilities.updateComponentTreeUI(this);
	}
	private void reinitMap() {
		if (editor != null) {
			editor.reinitMap();
		}	
	}
	private void updateTilesStyle() {
		setTilesStyle();
		reinitMap();
	}
	private void setTilesStyle() {
		switch(prefs.getInt(TilesDisplayStyle)) {
		case TileComponent.DISPLAY_TILES_STYLE_LEGENDARY:
			TileComponent.displayTilesStyle = TileComponent.DISPLAY_TILES_STYLE_LEGENDARY;
			break;
		case TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS:
			TileComponent.displayTilesStyle = TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS;
			break;
		default:
			TileComponent.displayTilesStyle = TileComponent.DISPLAY_TILES_STYLE_CLASSIC;
			break;
		}
	}
	private void setChitDisplayStyle() {
		switch(prefs.getInt(ChitDisplayStyle)) {
		case RealmComponent.DISPLAY_STYLE_CLASSIC:
			RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_CLASSIC;
			break;
		case RealmComponent.DISPLAY_STYLE_COLOR:
			RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_COLOR;
			break;
		case RealmComponent.DISPLAY_STYLE_FRENZEL:
			RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_FRENZEL;
			break;
		case RealmComponent.DISPLAY_STYLE_LEGENDARY:
			RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_LEGENDARY;
			break;
		default:
			RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_CLASSIC;
			break;
		}
	}
	private void setCharacterChitDisplayStyle() {
		switch(prefs.getInt(CharacterChitDisplayStyle)) {
		case CharacterChitComponent.DISPLAY_STYLE_CLASSIC:
			CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_CLASSIC;
			break;
		case CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC:
			CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC;
			break;
		case CharacterChitComponent.DISPLAY_STYLE_LEGENDARY:
			CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_LEGENDARY;
			break;
		default:
			CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_CLASSIC;
			break;
		}
	}
	private JMenuBar buildMenuBar() {
		JMenuBar menu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		openGame = new JMenuItem("Open Game");
		openGame.setMnemonic(KeyEvent.VK_O);
		openGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				openGame();
			}
		});
		fileMenu.add(openGame);
		closeGame = new JMenuItem("Close Game");
		closeGame.setMnemonic(KeyEvent.VK_C);
		closeGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				closeGame();
			}
		});
		fileMenu.add(closeGame);
		saveGame = new JMenuItem("Save Game");
		saveGame.setMnemonic(KeyEvent.VK_S);
		saveGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		saveGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveGame(false);
			}
		});
		fileMenu.add(saveGame);
		saveAsGame = new JMenuItem("Save Game As...");
		saveAsGame.setMnemonic(KeyEvent.VK_A);
		saveAsGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveGame(true);
			}
		});
		fileMenu.add(saveAsGame);
		fileMenu.add(new JSeparator());
		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_X);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				close();
			}
		});
		fileMenu.add(exit);
		menu.add(fileMenu);
		JMenu optionMenu = new JMenu("Options");
		final JCheckBoxMenuItem toggleLookAndFeel = new JCheckBoxMenuItem("Cross Platform Look and Feel",prefs.getBoolean(MetalLookAndFeel));
		toggleLookAndFeel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(MetalLookAndFeel,toggleLookAndFeel.isSelected());
				updateLookAndFeel();
			}
		});
		optionMenu.add(toggleLookAndFeel);
		JPanel gameButtons = new JPanel(new GridLayout(1,1));
		gameOptions = new JButton("Game Options");
		gameOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				HostGameSetupDialog setup = new HostGameSetupDialog(new JFrame(),"Game Options",editor.getGameData());
				setup.loadPrefsFromData();
				setup.setVisible(true);
			}
		});
		gameButtons.add(gameOptions);
		
		optionMenu.add(getTilesOptionsPanel());
		optionMenu.add(getChitOptionsPanel());
		optionMenu.add(getCharacterChitOptionsPanel());
		optionMenu.add(gameButtons);
		menu.add(optionMenu);
		return menu;
	}
	private JPanel getTilesOptionsPanel() {
		int selected = prefs.getInt(TilesDisplayStyle);
		JPanel panel = new JPanel(new GridLayout(3,1));
		panel.setBorder(BorderFactory.createTitledBorder("Standard Tiles Style"));
		ButtonGroup group = new ButtonGroup();
		JRadioButton classicTilesOption = new JRadioButton("Classic");
		classicTilesOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(TilesDisplayStyle,TileComponent.DISPLAY_TILES_STYLE_CLASSIC);
				updateTilesStyle();
			}
		});
		if (selected == TileComponent.DISPLAY_TILES_STYLE_CLASSIC) {
			classicTilesOption.setSelected(true);
		}
		group.add(classicTilesOption);
		panel.add(classicTilesOption);
		JRadioButton legendaryTilesOption = new JRadioButton("Legendary Realm");
		legendaryTilesOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(TilesDisplayStyle,TileComponent.DISPLAY_TILES_STYLE_LEGENDARY);
				updateTilesStyle();
			}
		});
		if (selected == TileComponent.DISPLAY_TILES_STYLE_LEGENDARY) {
			legendaryTilesOption.setSelected(true);
		}
		group.add(legendaryTilesOption);
		panel.add(legendaryTilesOption);
		JRadioButton legendaryWithIconsTilesOption = new JRadioButton("Legendary Realm (with Icons)");
		legendaryWithIconsTilesOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(TilesDisplayStyle,TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS);
				updateTilesStyle();
			}
		});
		if (selected == TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS) {
			legendaryWithIconsTilesOption.setSelected(true);
		}
		group.add(legendaryWithIconsTilesOption);
		panel.add(legendaryWithIconsTilesOption);
		return panel;
	}
	private JPanel getChitOptionsPanel() {
		int selected = prefs.getInt(ChitDisplayStyle);
		JPanel panel = new JPanel(new GridLayout(4,1));
		panel.setBorder(BorderFactory.createTitledBorder("Game Chits"));
		ButtonGroup group = new ButtonGroup();
		classicChitsOption = new JRadioButton("Classic Chits");
		classicChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(ChitDisplayStyle,RealmComponent.DISPLAY_STYLE_CLASSIC);
				setChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == RealmComponent.DISPLAY_STYLE_CLASSIC) {
			classicChitsOption.setSelected(true);
		}
		group.add(classicChitsOption);
		panel.add(classicChitsOption);
		colorChitsOption = new JRadioButton("Color Chits");
		colorChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(ChitDisplayStyle,RealmComponent.DISPLAY_STYLE_COLOR);
				setChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == RealmComponent.DISPLAY_STYLE_COLOR) {
			colorChitsOption.setSelected(true);
		}
		group.add(colorChitsOption);
		panel.add(colorChitsOption);
		frenzelChitsOption = new JRadioButton("Remodeled Chits");
		frenzelChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(ChitDisplayStyle,RealmComponent.DISPLAY_STYLE_FRENZEL);
				setChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == RealmComponent.DISPLAY_STYLE_FRENZEL) {
			frenzelChitsOption.setSelected(true);
		}
		group.add(frenzelChitsOption);
		panel.add(frenzelChitsOption);
		legendaryChitsOption = new JRadioButton("Legendary Chits");
		legendaryChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(ChitDisplayStyle,RealmComponent.DISPLAY_STYLE_LEGENDARY);
				setChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == RealmComponent.DISPLAY_STYLE_LEGENDARY) {
			legendaryChitsOption.setSelected(true);
		}
		legendaryChitsOption.setEnabled(false);
		group.add(legendaryChitsOption);
		panel.add(legendaryChitsOption);
		return panel;
	}
	private JPanel getCharacterChitOptionsPanel() {
		int selected = prefs.getInt(CharacterChitDisplayStyle);
		JPanel panel = new JPanel(new GridLayout(3,1));
		panel.setBorder(BorderFactory.createTitledBorder("Character Game Chits Style"));
		ButtonGroup group = new ButtonGroup();
		classicCharacterChitsOption = new JRadioButton("Classic");
		classicCharacterChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(CharacterChitDisplayStyle,CharacterChitComponent.DISPLAY_STYLE_CLASSIC);
				setCharacterChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == CharacterChitComponent.DISPLAY_STYLE_CLASSIC) {
			classicCharacterChitsOption.setSelected(true);
		}
		group.add(classicCharacterChitsOption);
		panel.add(classicCharacterChitsOption);
		legendaryClassicCharacterChitsOption = new JRadioButton("Legendary (classic hidden)");
		legendaryClassicCharacterChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(CharacterChitDisplayStyle,CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC);
				setCharacterChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC) {
			legendaryClassicCharacterChitsOption.setSelected(true);
		}
		group.add(legendaryClassicCharacterChitsOption);
		panel.add(legendaryClassicCharacterChitsOption);
		legendaryCharacterChitsOption = new JRadioButton("Legendary");
		legendaryCharacterChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(CharacterChitDisplayStyle,CharacterChitComponent.DISPLAY_STYLE_LEGENDARY);
				setCharacterChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == CharacterChitComponent.DISPLAY_STYLE_LEGENDARY) {
			legendaryCharacterChitsOption.setSelected(true);
		}
		group.add(legendaryCharacterChitsOption);
		panel.add(legendaryCharacterChitsOption);
		return panel;
	}
	private void closeGame() {
		if (validateOkayToClose("Close Game")) {
			editor.setVisible(false);
			desktop.remove(editor);
			editor = null;
			updateControls();
		}
	}
	private boolean validateOkayToClose(String title) {
		if (editor.getGameData().isModified()) {
			int ret = JOptionPane.showConfirmDialog(this,"The current game hasn't been saved.  Save now?",title,JOptionPane.YES_NO_CANCEL_OPTION);
			if (ret==JOptionPane.YES_OPTION) {
				saveGame(false);
			}
			else if (ret==JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		return true;
	}
	private void openGame() {
		JFileChooser chooser;
		String lastSaveGame = prefs.get(PreferredFilePath);
		if (lastSaveGame!=null) {
			String filePath = FileUtilities.getFilePathString(new File(lastSaveGame),false,false);
			chooser = new JFileChooser(new File(filePath));
		}
		else {
			chooser = new JFileChooser();
		}
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(saveGameFileFilter);
		if (chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
			File file = FileUtilities.fixFileExtension(chooser.getSelectedFile(),".rsgame");
			prefs.set(PreferredFilePath,file.getAbsolutePath());
			GameData gameData = new GameData();
			gameData.zipFromFile(file);
//			gameData.setTracksChanges(true);
			addGame(FileUtilities.getFilename(file,true),gameData);
		}
		updateControls();
	}
	private File queryFileName() {
		JFileChooser chooser;
		String lastSaveGame = prefs.get(PreferredFilePath);
		if (lastSaveGame!=null) {
			String filePath = FileUtilities.getFilePathString(new File(lastSaveGame),false,false);
			chooser = new JFileChooser(new File(filePath));
			chooser.setSelectedFile(new File(lastSaveGame));
		}
		else {
			chooser = new JFileChooser();
		}
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(saveGameFileFilter);
		if (chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
			File file = FileUtilities.fixFileExtension(chooser.getSelectedFile(),".rsgame");
			prefs.set(PreferredFilePath,file.getAbsolutePath());
			return file;
		}
		return null;
	}
	private void saveGame(boolean queryFilename) {
		File file;
		String lastSaveGame = prefs.get(PreferredFilePath);
		if (queryFilename || lastSaveGame==null) {
			file = queryFileName();
		}
		else {
			file = new File(lastSaveGame);
		}
		if (file!=null) {
			editor.setTitle(FileUtilities.getFilename(file,true));
			editor.getGameData().zipToFile(file);
			editor.getGameData().commit();
			updateControls();
		}
	}
	private void addGame(String title,GameData gameData) {
		if (editor!=null) {
			closeGame();
		}
		
		editor = new RealmGameEditor(this,title,gameData);
		editor.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		editor.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				closeGame();
			}
		});
		desktop.add(editor);
		editor.setVisible(true);
		try {
			editor.setSelected(true);
			editor.setMaximum(true);
		}
		catch(PropertyVetoException ex) {
			ex.printStackTrace();
		}
	}
	private void close() {
		savePrefs();
		setVisible(false);
		System.exit(0);
	}
	public static void main(String[] args) {
		RealmCharacterBuilderModel.loadAllCustomCharacters();
		RealmUtility.setupTextType();
		final RealmGmFrame frame = new RealmGmFrame();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				frame.close();
			}
		});
		frame.setVisible(true);
	}
}