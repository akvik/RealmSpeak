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
package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmSpeak.RealmGameHandler;
import com.robin.magic_realm.RealmSpeak.RealmSpeakFrame;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardRegenerateDenizen extends QuestReward {
	
	public static final String DENIZEN_REGEX = "_drx";
	
	public QuestRewardRegenerateDenizen(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> denizens = character.getGameData().getGameObjectsByNameRegex(getDenizenNameRegex());
		for (GameObject denizen : denizens) {
			if (denizen != null && denizen.hasThisAttribute("denizen") && !denizen.hasThisAttribute(Constants.CLONED) && !denizen.hasThisAttribute(Constants.COMPANION)) {			
				SetupCardUtility.resetDenizen(denizen);
			}
		}
		RealmSpeakFrame realmSpeakFrame = (RealmSpeakFrame) frame;
		RealmGameHandler gameHandler = realmSpeakFrame.getGameHandler();
		if (gameHandler!=null) {
			gameHandler.getInspector().redrawMap();
		}
	}
	
	public String getDescription() {
		return getDenizenNameRegex() +" is/are regenerated.";
	}
	
	public String getDenizenNameRegex() {
		return getString(DENIZEN_REGEX);
	}

	public RewardType getRewardType() {
		return RewardType.RegenerateDenizen;
	}
}