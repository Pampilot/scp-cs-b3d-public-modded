Function FillRoom%(r.Rooms)
	CatchErrors("FillRoom()")
	
	Local d.Doors, d2.Doors, sc.SecurityCams, de.Decals, r2.Rooms, fr.Forest, elev.Elevator
	Local it.Items, it2.Items, em.Emitters, w.WayPoints, w2.WayPoints, l.Lights
	Local xTemp#, yTemp#, zTemp#, xTemp2%, yTemp2%, zTemp2%, SF%, b%, Name$
	Local t1%, Tex%, Screen%, Scale#
	Local i%, k%, Temp%, Temp3%, Angle#
	Local ItemName$, ItemTempName$
	Local SinValue#, CosValue#
	
	Select(r\RoomTemplate\RoomID)
		Case r_room1_archive
			;[Block]
			; ~ Storage Room 6H door
			d.Doors = CreateDoor(r, r\x, r\y, r\z - 512.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_2)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 256.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 640.0 * RoomScale, 20.0)
			sc\Angle = 180.0 : sc\Turn = 45.0
			
			For xTemp2 = 0 To 1
				For yTemp2 = 0 To 2
					For zTemp2 = 0 To 2
						ItemName = "9V Battery" : ItemTempName = "bat"
						
						Local ItemChance% = Rand(-10, 100)
						
						Select(True)
							Case ItemChance < 0
								;[Block]
								Exit
								;[End Block]
							Case ItemChance > 0 And ItemChance < 45 ; ~ 5% chance for a key card
								;[Block]
								Temp3 = Rand(0, 2)
								ItemName = "Level " + Str(Temp3) + " Key Card"
								ItemTempName = "key" + Str(Temp3)
								;[End Block]
							Case ItemChance >= 45 And ItemChance < 50 ; ~ 5% chance for a medkit
								;[Block]
								ItemName = "First Aid Kit"
								ItemTempName = "firstaid"
								;[End Block]
							Case ItemChance >= 50 And ItemChance < 60 ; ~ 10% chance for a battery
								;[Block]
								ItemName = "9V Battery"
								ItemTempName = "bat"
								;[End Block]
							Case ItemChance >= 60 And ItemChance < 70 ; ~ 10% chance for an S-NAV
								;[Block]
								ItemName = "S-NAV Navigator"
								ItemTempName = "nav"
								;[End Block]
							Case ItemChance >= 70 And ItemChance < 85 ; ~ 15% chance for a radio
								;[Block]
								ItemName = "Radio Transceiver"
								ItemTempName = "radio"
								;[End Block]
							Case ItemChance >= 85 And ItemChance < 95 ; ~ 10% chance for a clipboard
								;[Block]
								ItemName = "Clipboard"
								ItemTempName = "clipboard"
								;[End Block]
							Case ItemChance >= 95 And ItemChance <= 100 ; ~ 5% chance for misc
								;[Block]
								Temp3 = Rand(2)
								Select(Temp3)
									Case 1 ; ~ Playing card
										;[Block]
										ItemName = "Playing Card"
										ItemTempName = "playcard"
										;[End Block]
									Case 2 ; ~ Mastercard
										;[Block]
										ItemName = "Mastercard"
										ItemTempName = Lower(ItemName)
										;[End Block]
								End Select
								;[End Block]
						End Select
						xTemp = (-672.0) + (864.0 * xTemp2)
						yTemp = 96.0 + (96.0 * yTemp2)
						zTemp = 480.0 - (352.0 * zTemp2) + Rnd(-96.0, 96.0)
						
						it.Items = CreateItem(ItemName, ItemTempName, r\x + xTemp * RoomScale, r\y + yTemp * RoomScale, r\z + zTemp * RoomScale)
						EntityParent(it\Collider, r\OBJ)
					Next
				Next
			Next
			
			CreateCustomCenter(r, r\x, r\z - 768.0 * RoomScale)
			;[End Block]
		Case r_room1_dead_end_lcz, r_room1_dead_end_ez
			;[Block]
			; ~ Evacuation shelter doors
			d.Doors = CreateDoor(r, r\x, r\y, r\z + 1202.0 * RoomScale, r\y, False, BIG_DOOR)
			d\MTFClose = False : d\DisableWaypoint = True
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[0] = d
			;[End Block]
		Case r_cont1_005
			;[Block]
			; ~ SCP-005 Chamber door
			d.Doors = CreateDoor(r, r\x, r\y, r\z - 640.0 * RoomScale, 0.0, I_005\ChanceToSpawn = 2, DEFAULT_DOOR, KEY_CARD_4)
			r\RoomDoors.Doors[0] = d
			
			sc.SecurityCams = CreateSecurityCam(r, r\x, r\y + 415.0 * RoomScale, r\z + 424.0 * RoomScale, 30.0)
			sc\Angle = 180.0 : sc\Turn = 30.0
			
;			it.Items = CreateItem("Document SCP-005", "paper", r\x + 504.0 * RoomScale, r\y + 152.0 * RoomScale, r\z - 500.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			If I_005\ChanceToSpawn = 1
				it.Items = CreateItem("SCP-005", "scp005", r\x, r\y + 255.0 * RoomScale, r\z + 238.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
				
				de.Decals = CreateDecal(DECAL_CORROSIVE_1, r\x - 362.0 * RoomScale, r\y + 0.005, r\z - 420.0 * RoomScale, 90.0, Rnd(360.0), 0.0)
				EntityParent(de\OBJ, r\OBJ)
			ElseIf I_005\ChanceToSpawn = 3
;				it.Items = CreateItem("Note from Maynard", "paper", r\x, r\y + 255.0 * RoomScale, r\z + 238.0 * RoomScale)
;				EntityParent(it\Collider, r\OBJ)
				
				de.Decals = CreateDecal(DECAL_BLOOD_2,  r\x + 380.0 * RoomScale, r\y + 150.0 * RoomScale, r\z - 875.0 * RoomScale, 0.0, r\Angle + 270.0, 0.0, 0.3)
				EntityParent(de\OBJ, r\OBJ)
			EndIf
			
			CreateCustomCenter(r, r\x, r\z - 830.0 * RoomScale)
			;[End Block]
		Case r_cont1_173
			;[Block]
			
			r\RoomAlarmRotors[0] = CreateAlarmRotor(r, "Alarm_Rotor", 4.0, False, r\x + 3900 * RoomScale, r\y + 850 * RoomScale, r\z + 1730 * RoomScale, 270, 90, 0, 200, 60, 0, 0.004, 0.02, 0.004, Alarm_Cone_X)
			
			r\RoomAlarmRotors[1] = CreateAlarmRotor(r, "Alarm_Rotor", 4.0, True, r\x + 4700 * RoomScale, r\y + 1120 * RoomScale, r\z + 1730 * RoomScale, 0, 0, 0, 255, 0, 0, 0.005, 0.03, 0.005, Alarm_Cone_Y)
			
;			r\Objects[2] = CopyEntity(elev_m_I\ElevatorModel[0], r\OBJ)
;			PositionEntity(r\Objects[2], 400, 0, 0)
;			EntityType r\Objects[2], HIT_MAP
;			EntityPickMode r\Objects[2], 2
;			
;			r\RoomDoors.Doors[3] = CreateDoor(r, r\x + 100.0 * RoomScale, r\y, r\z, 90.0, True, ELEVATOR_DOOR)
;			
;			elev = CreateElevator(r\Objects[2], 3, r\RoomDoors[3], 0, r, -5000, -2500, 0)
			
			r\RoomFluLights[0] = CreateFluLight(r, "Flu_Light", 0, 255, 255, 255, r\x, r\y + 360.0 * RoomScale, r\z)
			r\RoomFluLights[1] = CreateFluLight(r, "Flu_Light2", 1, 0, 200, 255, r\x + 200.0 * RoomScale, r\y + 320.0 * RoomScale, r\z)
			r\RoomFluLights[2] = CreateFluLight(r, "Flu_Light2", 0, 200, 100, 25, r\x + 400.0 * RoomScale, r\y + 320.0 * RoomScale, r\z)
			
			InitFluLight(r, 0, FLU_STATE_FLICKER)
			InitFluLight(r, 1, FLU_STATE_ON)
			
			r\RoomProjectors[0] = CreateProjector(r, "GFX\Map\Rooms\projector_wall.b3d", r\x - 300.0 * RoomScale, r\y, r\z + 1000.0 * RoomScale, 0.0, 0.0, 0.0, "GFX\Map\Textures\projector_graph.png", -0.5, 0.8, 0.0, 45.0, 0.0, 0.0)
			r\RoomProjectors[1] = CreateProjector(r, "GFX\Map\Rooms\projector_wall.b3d", r\x + 400.0 * RoomScale, r\y, r\z + 1000.0 * RoomScale, 0.0, 0.0, 0.0, "GFX\Map\Textures\projector_graph_2.png", 0.0, 0.5, 0.0, 0.0, 0.0, 0.0)
			
			; ~ Misc doors
			d.Doors = CreateDoor(r, r\x - 640.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 95.0 * RoomScale, -90.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x + 1166.0 * RoomScale, r\y + 383.9 * RoomScale, r\z + 339.0 * RoomScale, 180.0, True, ONE_SIDED_DOOR)
			d\Locked = 1 : d\MTFClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) - 0.048, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.048, True)
			
			d.Doors = CreateDoor(r, r\x, r\y, r\z + 1214.0 * RoomScale, 180.0, False, DEFAULT_DOOR, KEY_CARD_3)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x + 2128.0 * RoomScale, r\y + 384.0 * RoomScale, r\z - 418.0 * RoomScale, 0.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			; ~ The big door leading to testing area
			d.Doors = CreateDoor(r, r\x + 4000.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 1726.0 * RoomScale, 90.0, True, BIG_DOOR)
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[0] = d
			
			; ~ The door leading to containment chamber
			d.Doors = CreateDoor(r, r\x + 2704.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 654.0 * RoomScale, 90.0)
			d\AutoClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[1] = d
			
			d.Doors = CreateDoor(r, r\x + 1392.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 95.0 * RoomScale, 90.0, True)
			d\Locked = 1 : d\MTFClose = False
			r\RoomDoors.Doors[2] = d
			
			r\Objects[0] = LoadMesh_Strict("GFX\Map\Props\table_a.b3d")
			ScaleEntity(r\Objects[0], RoomScale, RoomScale, RoomScale)
			RotateEntity(r\Objects[0], 90.0, 45.0, 0.0)
			PositionEntity(r\Objects[0], r\x + 272.0 * RoomScale, r\y + 10.0 * RoomScale, r\z + 206.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = LoadMesh_Strict("GFX\Map\Props\cabinet_c.b3d")
			ScaleEntity(r\Objects[1], RoomScale, RoomScale, RoomScale)
			RotateEntity(r\Objects[1], 0.0, 225.0, 90.0)
			PositionEntity(r\Objects[1], r\x + 448.0 * RoomScale, r\y + 10.0 * RoomScale, r\z + 102.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 516.0 * RoomScale, r\y + 352.0 * RoomScale, r\z + 95.0 * RoomScale, 20.0, True, r\x + 1454.0 * RoomScale, r\y + 608.0 * RoomScale, r\z + 414.0 * RoomScale, 0.0, 90.0, 0.0)
			sc\Angle = 270.0 : sc\Turn = 45.0
			
			de.Decals = CreateDecal(DECAL_CORROSIVE_1, r\x + 272.0 * RoomScale, r\y + 0.005, r\z + 262.0 * RoomScale, 90.0, Rnd(360.0), 0.0)
			EntityParent(de\OBJ, r\OBJ)
			
			de.Decals = CreateDecal(DECAL_CORROSIVE_1, r\x + 456.0 * RoomScale, r\y + 0.005, r\z + 135.0 * RoomScale, 90.0, Rnd(360.0), 0.0)
			EntityParent(de\OBJ, r\OBJ)
			
			For i = 0 To 4
				Select(i)
					Case 0
						;[Block]
						xTemp = 4299.0
						zTemp = 1268.0
						Temp = DECAL_BLOOD_3
						;[End Block]
					Case 1
						;[Block]
						xTemp = 5184.0
						zTemp = 2254.0
						Temp = DECAL_BLOOD_3
						;[End Block]
					Case 2
						;[Block]
						xTemp = 5216.0
						zTemp = 1262.0
						Temp = DECAL_BLOOD_3
						;[End Block]
					Case 3
						;[Block]
						xTemp = 4314.0 
						zTemp = 1985.0
						Temp = DECAL_BLOOD_3
						;[End Block]
					Case 4
						;[Block]
						xTemp = 4972.0
						zTemp = 1969.0
						Temp = DECAL_BLOOD_5
						;[End Block]
				End Select
				de.Decals = CreateDecal(Temp, r\x + xTemp * RoomScale, r\y + 386.0 * RoomScale, r\z + zTemp * RoomScale, 90.0, 45.0, 0.0, ((i = 0) * 0.44) + ((i = 1) * 1.2) + ((i > 1) * 0.54), Rnd(0.8, 1.0))
				EntityParent(de\OBJ, r\OBJ)
			Next
			;[End Block]
		Case r_cont1_173_intro
			;[Block]
			; ~ Misc doors
			d.Doors = CreateDoor(r, r\x - 2784 * RoomScale, r\y, r\z + 768.0 * RoomScale, 90.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 5760.0 * RoomScale, r\y, r\z + 1216.0 * RoomScale, 180.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 5760.0 * RoomScale, r\y, r\z + 320.0 * RoomScale, 0.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 8064.0 * RoomScale, r\y, r\z + 1216.0 * RoomScale, 180.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 8736.0 * RoomScale, r\y, r\z + 768.0 * RoomScale, 270.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 8064.0 * RoomScale, r\y, r\z - 2272.0 * RoomScale, 0.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 3424.0 * RoomScale, r\y - 384.0 * RoomScale, r\z - 2367.0 * RoomScale, 0.0)
			d\AutoClose = False : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 1296.0 * RoomScale, r\y, r\z - 1761.0 * RoomScale, 0.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 8064.0 * RoomScale, r\y, r\z + 320.0 * RoomScale, 0.0, True)
			d\Locked = 1 : d\MTFClose = False
			
			d.Doors = CreateDoor(r, r\x - 6496.0 * RoomScale, r\y, r\z - 1248.0 * RoomScale, 90.0, True)
			d\Locked = 1 : d\MTFClose = False
			
			d.Doors = CreateDoor(r, r\x - 4064.0 * RoomScale, r\y, r\z - 1248.0 * RoomScale, 90.0, True)
			d\Locked = 1 : d\MTFClose = False
			
			d.Doors = CreateDoor(r, r\x - 2258.0 * RoomScale, r\y, r\z - 1004.0 * RoomScale, 180.0, False, ONE_SIDED_DOOR)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.048, True)
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			; ~ The big door leading to testing area
			d.Doors = CreateDoor(r, r\x + 576.0 * RoomScale, r\y, r\z + 383.0 * RoomScale, 90.0, False, BIG_DOOR)
			d\MTFClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[1] = d
			
			; ~ The door leading to containment chamber
			d.Doors = CreateDoor(r, r\x - 720.0 * RoomScale, r\y, r\z - 689.0 * RoomScale, 90.0)
			d\Locked = 1 : d\MTFClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[2] = d
			
			; ~ The door leading to chamber entrance
			d.Doors = CreateDoor(r, r\x - 2032.0 * RoomScale, r\y, r\z - 1248.0 * RoomScale, 90.0, True)
			d\Locked = 1 : d\MTFClose = False
			r\RoomDoors.Doors[3] = d
			
			; ~ The door leading to 3-11 cell
			d.Doors = CreateDoor(r, r\x - 4096.0 * RoomScale, r\y, r\z + 512.0 * RoomScale, 0.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			Tex = LoadTexture_Strict("GFX\Map\Textures\Door02.jpg")
			If opt\Atmosphere Then TextureBlend(Tex, 5)
			EntityTexture(d\OBJ, Tex)
			DeleteSingleTextureEntryFromCache(Tex)
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[4] = d
			
			; ~ The door in the office leading to observarion room
			d.Doors = CreateDoor(r, r\x - 3424.0 * RoomScale, r\y - 384.0 * RoomScale, r\z - 129.0 * RoomScale, 0.0, True, DEFAULT_DOOR, KEY_CARD_3)
			d\MTFClose = False
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			r\RoomDoors.Doors[5] = d
			
			; ~ Balcony guard spawnpoint
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 328.0 * RoomScale, r\y + 480.0 * RoomScale, r\z + 1072.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			; ~ Class-D spawnpoint
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 208.0 * RoomScale, r\y + 0.3, r\z + 480.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x + 160.0 * RoomScale, r\y + 0.3, r\z + 320.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 948.0 * RoomScale, r\y + 0.3, r\z + 526.0 * RoomScale)
			EntityParent(r\Objects[3], r\OBJ)
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x + 980.0 * RoomScale, r\y + 0.3, r\z + 320.0 * RoomScale)
			EntityParent(r\Objects[4], r\OBJ)
			
			; ~ Player spawnpoint
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x - 4096.0 * RoomScale, r\y + 0.3, r\z)
			EntityParent(r\Objects[5], r\OBJ)
			
			; ~ SCP-173 spawnpoint
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x + 1760.0 * RoomScale, r\y + 0.4, r\z + 912.0 * RoomScale)
			EntityParent(r\Objects[6], r\OBJ)
			
			; ~ Guard conwoy #1 spawnpoint
			r\Objects[7] = CreatePivot()
			PositionEntity(r\Objects[7], r\x - 4096.0 * RoomScale + Rnd(-0.3, 0.3), r\y + 0.3, r\z + Rnd(860.0, 896.0) * RoomScale)
			EntityParent(r\Objects[7], r\OBJ)
			
			r\Objects[8] = CreatePivot()
			PositionEntity(r\Objects[8], r\x - 3840.0 * RoomScale + Rnd(-0.3, 0.3), r\y + 0.3, r\z + 786.0 * RoomScale)
			EntityParent(r\Objects[8], r\OBJ)
			
			; ~ Music guard spawnpoint
			r\Objects[9] = CreatePivot()
			PositionEntity(r\Objects[9], r\x - 8064.0 * RoomScale, r\y + 0.3, r\z + 1096.0 * RoomScale)
			EntityParent(r\Objects[9], r\OBJ)
			
			; ~ Franklin spawnpoint
			r\Objects[10] = CreatePivot()
			PositionEntity(r\Objects[10], r\x - 3424.0 * RoomScale, r\y - 0.3, r\z - 2208.0 * RoomScale)
			EntityParent(r\Objects[10], r\OBJ)
			
			; ~ Sitting scientist spawnpoint
			r\Objects[11] = CreatePivot()
			PositionEntity(r\Objects[11], r\x - 3073.0 * RoomScale, r\y - 315.0 * RoomScale, r\z - 2165.0 * RoomScale)
			EntityParent(r\Objects[11], r\OBJ)
			
			; ~ Guard convoy #2 spawnpoint
			r\Objects[12] = CreatePivot()
			PositionEntity(r\Objects[12], r\x - 3800.0 * RoomScale, r\y + 1.1, r\z - 4088.0 * RoomScale)
			EntityParent(r\Objects[12], r\OBJ)
			
			r\Objects[13] = CreatePivot()
			PositionEntity(r\Objects[13], r\x - 4200.0 * RoomScale, r\y + 1.1, r\z - 4088.0 * RoomScale)
			EntityParent(r\Objects[13], r\OBJ)
			
			; ~ Class-D inside convoy
			r\Objects[14] = CreatePivot()
			PositionEntity(r\Objects[14], r\x - 4000.0 * RoomScale, r\y + 1.1, r\z - 4088.0 * RoomScale)
			EntityParent(r\Objects[14], r\OBJ)
			
			r\Objects[15] = CreatePivot()
			PositionEntity(r\Objects[15], r\x - 7208.0 * RoomScale, r\y - 0.6, r\z - 3104.0 * RoomScale)
			EntityParent(r\Objects[15], r\OBJ)
			
			; ~ Sitting Frankling spawnpoint
			r\Objects[16] = CreatePivot()
			PositionEntity(r\Objects[16], r\x - 902.0 * RoomScale, r\y + 500.0 * RoomScale, r\z + 456.0 * RoomScale)
			EntityParent(r\Objects[16], r\OBJ)
			
			; ~ Balcony guard positon after breach
			r\Objects[17] = CreatePivot()
			PositionEntity(r\Objects[17], r\x + 128.0 * RoomScale, r\y + 480.0 * RoomScale, r\z + 1280.0 * RoomScale)
			EntityParent(r\Objects[17], r\OBJ)
			
			; ~ SCP-173 position after breach
			r\Objects[18] = CreatePivot()
			PositionEntity(r\Objects[18], r\x - 320.0 * RoomScale, r\y + 480.0 * RoomScale, r\z + 1312.0 * RoomScale)
			EntityParent(r\Objects[18], r\OBJ)
			
			For i = 0 To 4
				Select(i)
					Case 0
						;[Block]
						xTemp = 875.0
						zTemp = -70.0
						Temp = DECAL_BLOOD_3
						;[End Block]
					Case 1
						;[Block]
						xTemp = 1760.0
						zTemp = 912.0
						Temp = DECAL_BLOOD_3
						;[End Block]
					Case 2
						;[Block]
						xTemp = 1792.0
						zTemp = -80.0
						Temp = DECAL_BLOOD_3
						;[End Block]
					Case 3
						;[Block]
						xTemp = 890.0
						zTemp = 642.0
						Temp = DECAL_BLOOD_3
						;[End Block]
					Case 4
						;[Block]
						xTemp = 1548.0
						zTemp = 627.0
						Temp = DECAL_BLOOD_5
						;[End Block]
				End Select
				de.Decals = CreateDecal(Temp, r\x + xTemp * RoomScale, r\y + 2.0 * RoomScale, r\z + zTemp * RoomScale, 90.0, 45.0, 0.0, ((i = 0) * 0.44) + ((i = 1) * 1.2) + ((i > 1) * 0.54), Rnd(0.8, 1.0))
			Next
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 3940.0 * RoomScale, r\y - 32.0 * RoomScale, r\z - 1248.0 * RoomScale, 20.0, True, r\x - 1970.0 * RoomScale, r\y + 224.0 * RoomScale, r\z - 928.0 * RoomScale, 0.0, 90.0, 0.0)
			sc\Angle = 270.0 : sc\Turn = 45.0
			
;			it.Items = CreateItem("Class D Orientation Leaflet", "paper", r\x - 3934.0 * RoomScale, r\y + 170.0 * RoomScale, r\z + 8.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_cont1_205
			;[Block]
			; ~ SCP-205 Chamber Door
			d.Doors = CreateDoor(r, r\x - 1400.0 * RoomScale, r\y - 128.0 * RoomScale, r\z - 384.0 * RoomScale, 0.0)
			d\AutoClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[0] = d
			
			; ~ Observation room doors
			d.Doors = CreateDoor(r, r\x + 128.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_2)
			r\RoomDoors.Doors[1] = d
			
			d.Doors = CreateDoor(r, r\x - 96.0 * RoomScale, r\y, r\z - 384.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_2)
			r\RoomDoors.Doors[2] = d
			
			; ~ Storage Room 1C Door
			d.Doors = CreateDoor(r, r\x + 472.0 * RoomScale, r\y, r\z - 384.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_1)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 1152.0 * RoomScale, r\y + 900.0 * RoomScale, r\z + 176.0 * RoomScale, 0.0, True, r\x - 1716.0 * RoomScale, r\y + 160.0 * RoomScale, r\z + 176.0 * RoomScale, 0.0, 90.0, 0.0)
			sc\Angle = 90.0 : sc\Turn = 0.0 : sc\AllowSaving = False : sc\RenderInterval = 0.0
			sc\ScriptedCamera = True : sc\ScriptedMonitor = True
			ScaleSprite(sc\ScrOBJ, 896.0 * 0.5 * RoomScale, 896.0 * 0.5 * RoomScale)
			CameraZoom(sc\Cam, 1.5)
			HideEntity(sc\BaseOBJ)
			HideEntity(sc\CameraOBJ)
			HideEntity(sc\MonitorOBJ)
			r\RoomSecurityCams[0] = sc
			
			; ~ Demons spawnpoint
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 1536.0 * RoomScale, r\y + 730.0 * RoomScale, r\z + 192.0 * RoomScale)
			RotateEntity(r\Objects[0], 0.0, -90.0, 0.0)
			EntityParent(r\Objects[0], r\OBJ)
			
			; ~ Demons trigger point
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 1400.0 * RoomScale, r\y, r\z + 192.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
;			it.Items = CreateItem("Document SCP-205", "paper", r\x - 357.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 150.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Ballistic Helmet", "helmet", r\x + 206.0 * RoomScale, r\y + 230.0 * RoomScale, r\z - 80.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("ReVision Eyedrops", "eyedrops", r\x + 206.0 * RoomScale, r\y + 190.0 * RoomScale, r\z + 180.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Level 3 Key Card", "key3", r\x - 975.0 * RoomScale, r\y - 10.0 * RoomScale, r\z + 650.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x + 188.0 * RoomScale, r\z - 724.0 * RoomScale)
			;[End Block]
		Case r_cont1_372
			;[Block]
			; ~ SCP-372 Chamber door
			d.Doors = CreateDoor(r, r\x, r\y, r\z - 368.0 * RoomScale, 0.0, False, BIG_DOOR, KEY_CARD_2)
			PositionEntity(d\Buttons[0], r\x - 496.0 * RoomScale, EntityY(d\Buttons[0], True), r\z - 278.0 * RoomScale, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.025, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			TurnEntity(d\Buttons[0], 0.0, 90.0, 0.0)
			
			; ~ Hit Box
			r\Objects[0] = LoadMesh_Strict("GFX\Map\Rooms\cont1_372_hb.b3d", r\OBJ)
			r\ScriptedObject[0] = True
			EntityPickMode(r\Objects[0], 2)
			EntityType(r\Objects[0], HIT_MAP)
			EntityAlpha(r\Objects[0], 0.0)
			
;			it.Items = CreateItem("Document SCP-372", "paper", r\x + 800.0 * RoomScale, r\y + 176.0 * RoomScale, r\z + 1108.0 * RoomScale)
;			RotateEntity(it\Collider, 0.0, r\Angle, 0.0)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Radio Transceiver", "radio", r\x + 800.0 * RoomScale, r\y + 112.0 * RoomScale, r\z + 944.0 * RoomScale)
			it\State = Rnd(100.0)
			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x, r\z - 704.0 * RoomScale)
			;[End Block]
		Case r_cont1_914
			;[Block]
			; ~ SCP-914 doors
			d.Doors = CreateDoor(r, r\x - 1037.0 * RoomScale, r\y, r\z + 528.0 * RoomScale, 180.0, True, SCP_914_DOOR)
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x + 404.0 * RoomScale, r\y, r\z + 528.0 * RoomScale, 180.0, True, SCP_914_DOOR)
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[1] = d
			
			; ~ SCP-914 Chamber Door
			d.Doors = CreateDoor(r, r\x, r\y, r\z - 368.0 * RoomScale, 0.0, False, BIG_DOOR, KEY_CARD_2)
			PositionEntity(d\Buttons[0], r\x - 496.0 * RoomScale, EntityY(d\Buttons[0], True), r\z - 278.0 * RoomScale, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.025, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			TurnEntity(d\Buttons[0], 0.0, 90.0, 0.0)
			r\RoomDoors.Doors[2] = d
			
			; ~ Observation room doors
			d.Doors = CreateDoor(r, r\x - 449.0 * RoomScale, r\y, r\z - 704.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_2)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.057, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.057, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			r\RoomDoors.Doors[3] = d
			
			r\Objects[0] = LoadMesh_Strict("GFX\Map\Props\scp_914_key.b3d")
			PositionEntity(r\Objects[0], r\x - 416.0 * RoomScale, r\y + 190.0 * RoomScale, r\z + 374.0 * RoomScale, True)
			
			r\Objects[1] = LoadMesh_Strict("GFX\Map\Props\scp_914_knob.b3d")
			PositionEntity(r\Objects[1], r\x - 416.0 * RoomScale, r\y + 230.0 * RoomScale, r\z + 374.0 * RoomScale, True)
			
			For i = 0 To 1
				ScaleEntity(r\Objects[i], RoomScale, RoomScale, RoomScale, True)
				EntityPickMode(r\Objects[i], 2)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 1128.0 * RoomScale, r\y + 0.5, r\z + 640.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 308.0 * RoomScale, r\y + 0.5, r\z + 640.0 * RoomScale)
			EntityParent(r\Objects[3], r\OBJ)
			
;			it.Items = CreateItem("Addendum: 5/14 Test Log", "paper", r\x + 538.0 * RoomScale, r\y + 178.0 * RoomScale, r\z + 127.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
;			RotateEntity(it\Collider, 0.0, 0.0, 0.0)
			
			it.Items = CreateItem("First Aid Kit", "firstaid", r\x + 538.0 * RoomScale, r\y + 112.0 * RoomScale, r\z - 40.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			
;			it.Items = CreateItem("Dr. L's Note #1", "paper", r\x - 538.0 * RoomScale, r\y + 250.0 * RoomScale, r\z - 365.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x, r\z - 704.0 * RoomScale)
			;[End Block]
		Case r_room2_2_lcz
			;[Block]
			d.Doors = CreateDoor(r, r\x + 672.0 * RoomScale, r\y, r\z, 90.0, False, DEFAULT_DOOR, KEY_CARD_2)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
            
			For r2.Rooms = Each Rooms
				If r2 <> r
					If r2\RoomTemplate\RoomID = r_room2_2_lcz
						r\Objects[0] = CopyEntity(r2\Objects[0]) ; ~ Don't load the mesh again
						Exit
					EndIf
				EndIf
			Next
			If r\Objects[0] = 0 Then r\Objects[0] = LoadRMesh("GFX\Map\Rooms\ventilation_fan.rmesh", Null, False)
			ScaleEntity(r\Objects[0], RoomScale, RoomScale, RoomScale)
			PositionEntity(r\Objects[0], r\x - 270.0 * RoomScale, r\y + 528.0 * RoomScale, r\z)
			EntityParent(r\Objects[0], r\OBJ)
			
			If Rand(2) = 1
				it.Items = CreateItem("Empty Cup", "emptycup", r\x + 490.0 * RoomScale, r\y + 150.0 * RoomScale, r\z + -232.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			;[End Block]
		Case r_room2_4_lcz
			;[Block]
			d.Doors = CreateDoor(r, r\x + 768.0 * RoomScale, r\y, r\z - 827.5 * RoomScale, 90.0, False, DEFAULT_DOOR)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.1, True)
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			;[End Block]
		Case r_room2_6_lcz
			;[Block]
			d.Doors = CreateDoor(r, r\x, r\y, r\z + 528.0 * RoomScale, 0.0)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], r\x - 998.0 * RoomScale, EntityY(d\Buttons[0], True), r\z, True)
			RotateEntity(d\Buttons[0], 0.0, 90.0, 0.0, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.061, True)
			
			d2.Doors = CreateDoor(r, r\x, r\y, r\z - 528.0 * RoomScale, 180.0, True)
			d2\AutoClose = False
			FreeEntity(d2\Buttons[0]) : d2\Buttons[0] = 0
			PositionEntity(d2\Buttons[1], EntityX(d2\Buttons[1], True), EntityY(d2\Buttons[1], True), EntityZ(d2\Buttons[1], True) + 0.061, True)
			
			d\LinkedDoor = d2
			d2\LinkedDoor = d
			;[End Block]
		Case r_room2_closets
			;[Block]
			d.Doors = CreateDoor(r, r\x - 244.0 * RoomScale, r\y, r\z, 90.0)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.048, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.048, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			d.Doors = CreateDoor(r, r\x - 1216.0 * RoomScale, r\y - 384.0 * RoomScale, r\z - 1024.0 * RoomScale, 0.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 1216.0 * RoomScale, r\y - 384.0 * RoomScale, r\z + 1024.0 * RoomScale, 180.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 232.0 * RoomScale, r\y - 384.0 * RoomScale, r\z - 644.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_1)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 232.0 * RoomScale, r\y - 384.0 * RoomScale, r\z + 644.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_1)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 1180.0 * RoomScale, r\y - 256.0 * RoomScale, r\z + 896.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 1292.0 * RoomScale, r\y - 256.0 * RoomScale, r\z - 160.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 1065.0 * RoomScale, r\y - 380.0 * RoomScale, r\z + 50.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x + 184.0 * RoomScale, r\y + 704.0 * RoomScale, r\z + 952.0 * RoomScale, 20.0)
			sc\Angle = 130.0 : sc\Turn = 40.0
			
			it.Items = CreateItem("Gas Mask", "gasmask", r\x + 736.0 * RoomScale, r\y + 176.0 * RoomScale, r\z + 544.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateRandomBattery(r\x + 736.0 * RoomScale, r\y + 100.0 * RoomScale, r\z - 448.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			If Rand(2) = 1
				it.Items = CreateRandomBattery(r\x + 730.0 * RoomScale, r\y + 176.0 * RoomScale, r\z - 580.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			If Rand(2) = 1
				it.Items = CreateRandomBattery(r\x + 740.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 750.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			it.Items = CreateItem("Level 0 Key Card", "key0", r\x + 736.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 752.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Incident Report SCP-1048-A", "paper", r\x + 736.0 * RoomScale, r\y + 224.0 * RoomScale, r\z - 480.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_elevator
			;[Block]
			d.Doors = CreateDoor(r, r\x + 448.0 * RoomScale, r\y, r\z, -90.0, True, MT_ELEVATOR_DOOR)
			d\MTFClose = False
			r\RoomDoors.Doors[0] = d
			;[End Block]
		Case r_room2_gw, r_room2_gw_2
			;[Block]
			d.Doors = CreateDoor(r, r\x + 338.0 * RoomScale, r\y, r\z - 385.0 * RoomScale, 0.0, True)
			d\Locked = 1 : d\MTFClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[0] = d
			
			d.Doors= CreateDoor(r, r\x + 338.0 * RoomScale, r\y, r\z + 462.0 * RoomScale, 180.0, True)
			d\Locked = 1 : d\MTFClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[1] = d
			
			d.Doors = CreateDoor(r, r\x - 458.0 * RoomScale, r\y, r\z + 732.0 * RoomScale, 90.0)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.04, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.04, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			If r\RoomTemplate\RoomID = r_room2_gw
				d.Doors = CreateDoor(r, r\x - 458.0 * RoomScale, r\y, r\z + 35.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_3)
				PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.04, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) - 1.035, True)
				PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.04, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.09, True)
			EndIf
			
			If r\RoomTemplate\RoomID = r_room2_gw Then r\RoomLevers.Levers[0] = CreateLever(r, r\x + 162.0 * RoomScale, r\y + 192.0 * RoomScale, r\z - 204.0 * RoomScale, 270.0, True)
			
			If r\RoomTemplate\RoomID = r_room2_gw_2
				r\Objects[0] = CreatePivot()
				PositionEntity(r\Objects[0], r\x + 262.0 * RoomScale, r\y + 325.0 * RoomScale, r\z - 345.0 * RoomScale)
				EntityParent(r\Objects[0], r\OBJ)
				
				r\Objects[1] = CreatePivot()
				PositionEntity(r\Objects[1], r\x - 447.0 * RoomScale, r\y + 177.0 * RoomScale, r\z - 98.0 * RoomScale)
				EntityParent(r\Objects[1], r\OBJ)
			Else
				r\Objects[0] = CreatePivot()
				PositionEntity(r\Objects[0], r\x + 344.0 * RoomScale, r\y + 128.0 * RoomScale, r\z)
				EntityParent(r\Objects[0], r\OBJ)
				
				Local BD_Temp%
				
				If bk\IsBroken Then BD_Temp = (bk\x = r\x And bk\z = r\z)
				
				If ((Not bk\IsBroken) And Rand(2) = 1) Lor BD_Temp
					r\Objects[1] = CopyEntity(d_I\DoorModelID[DOOR_DEFAULT_MODEL])
					ScaleEntity(r\Objects[1], (204.0 * RoomScale) / MeshWidth(r\Objects[1]), 313.0 * RoomScale / MeshHeight(r\Objects[1]), 16.0 * RoomScale / MeshDepth(r\Objects[1]))
					EntityType(r\Objects[1], HIT_MAP)
					PositionEntity(r\Objects[1], r\x + 336.0 * RoomScale, r\y, r\z + 462.0 * RoomScale)
					RotateEntity(r\Objects[1], 0.0, 180.0 + 180.0, 0.0)
					EntityParent(r\Objects[1], r\OBJ)
					MoveEntity(r\Objects[1], 120.0, 0.0, 5.0)
					
					bk\IsBroken = True
					bk\x = r\x
					bk\z = r\z
					
					FreeEntity(r\RoomDoors[1]\OBJ2) : r\RoomDoors[1]\OBJ2 = 0
				EndIf
			EndIf
			
			CreateCustomCenter(r, r\x + 336.0 * RoomScale, r\z + 32.0 * RoomScale)
		Case r_room2_js
			;[Block]
			; ~ Janitorial Lockers
			d.Doors = CreateDoor(r, r\x + 288.0 * RoomScale, r\y, r\z + 576.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_0)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x + 1646.0 * RoomScale, r\y + 435.0 * RoomScale, r\z + 193.0 * RoomScale, 20.0)
			sc\Angle = 30.0 : sc\Turn = 30.0
			
			de.Decals = CreateDecal(DECAL_WATER, r\x + 103.0 * RoomScale, r\y + 0.005, r\z + 161.0 * RoomScale, 90.0, Rnd(360.0), 0.0, Rnd(0.8, 1.0), 1.0)
			EntityParent(de\OBJ, r\OBJ)
			
			it.Items = CreateItem("Level 1 Key Card", "key1", r\x + 1715.0 * RoomScale, r\y + 250.0 * RoomScale, r\z + 718.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Origami", "origami", r\x + 1467.0 * RoomScale, r\y + 250.0 * RoomScale, r\z + 961.0 * RoomScale)
;			RotateEntity(it\Collider, 0.0, 0.0, 0.0)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateRandomBattery(r\x + 1714.0 * RoomScale, r\y + 250.0 * RoomScale, r\z + 936.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_sl
			;[Block]
			; ~ Doors for surveillance room
			d.Doors = CreateDoor(r, r\x + 480.0 * RoomScale, r\y, r\z - 640.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_3)
			d\MTFClose = False
			PositionEntity(d\Buttons[0], r\x + 576.0 * RoomScale, EntityY(d\Buttons[0], True), r\z - 474.0 * RoomScale, True)
			RotateEntity(d\Buttons[0], 0.0, 270.0, 0.0)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x + 544.0 * RoomScale, r\y + 480.0 * RoomScale, r\z + 256.0 * RoomScale, 270.0, False, ONE_SIDED_DOOR, KEY_CARD_3)
			d\MTFClose = False
			r\RoomDoors.Doors[1] = d
			
			; ~ Misc. door
			d.Doors = CreateDoor(r, r\x + 1504.0 * RoomScale, r\y + 480.0 * RoomScale, r\z + 960.0 * RoomScale, 180.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			r\RoomLevers.Levers[0] = CreateLever(r, r\x - 49.0 * RoomScale, r\y + 689.0 * RoomScale, r\z + 913.0 * RoomScale, 0.0, True)
			
			Scale = RoomScale * 4.5 * 0.4
			
			r\Textures[0] = LoadAnimTexture_Strict("GFX\Overlays\SL_monitors_checkpoint.png", 1, 512, 512, 0, 4, DeleteAllTextures)
			r\Textures[1] = LoadAnimTexture_Strict("GFX\Overlays\SL_monitors.png", 1, 512, 512, 0, 10, DeleteAllTextures)
			
			; ~ Monitor Objects
			For i = 0 To 14
				If i <> 7
					r\Objects[i] = CopyEntity(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL])
					ScaleEntity(r\Objects[i], Scale, Scale, Scale)
					If i <> 4 And i <> 13
						Screen = CreateSprite()
						EntityFX(Screen, 17)
						SpriteViewMode(Screen, 2)
						ScaleSprite(Screen, MeshWidth(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL]) * Scale * 0.95 * 0.5, MeshHeight(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL]) * Scale * 0.95 * 0.5)
						Select(i)
							Case 0
								;[Block]
								EntityTexture(Screen, r\Textures[1], 0)
								;[End Block]
							Case 1
								;[Block]
								EntityTexture(Screen, r\Textures[1], 9)
								;[End Block]
							Case 2
								;[Block]
								EntityTexture(Screen, r\Textures[1], 2)
								;[End Block]
							Case 3
								;[Block]
								EntityTexture(Screen, r\Textures[1], 1)
								;[End Block]
							Case 5
								;[Block]
								EntityTexture(Screen, r\Textures[1], 8)
								;[End Block]
							Case 8
								;[Block]
								EntityTexture(Screen, r\Textures[1], 4)
								;[End Block]
							Case 9
								;[Block]
								EntityTexture(Screen, r\Textures[1], 5)
								;[End Block]
							Case 10
								;[Block]
								EntityTexture(Screen, r\Textures[1], 3)
								;[End Block]
							Case 11
								;[Block]
								EntityTexture(Screen, r\Textures[1], 7)
								;[End Block]
							Default
								;[Block]
								EntityTexture(Screen, r\Textures[0], 3)
								;[End Block]
						End Select
						EntityParent(Screen, r\Objects[i])
					ElseIf i = 4
						r\Objects[18] = CreateSprite()
						EntityFX(r\Objects[18], 17)
						SpriteViewMode(r\Objects[18], 2)
						ScaleSprite(r\Objects[18], MeshWidth(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL]) * Scale * 0.95 * 0.5, MeshHeight(mon_I\MonitorModelID[0]) * Scale * 0.95 * 0.5)
						EntityTexture(r\Objects[18], r\Textures[0], 2)
						EntityParent(r\Objects[18], r\Objects[i])
					Else
						r\Objects[19] = CreateSprite()
						EntityFX(r\Objects[19], 17)
						SpriteViewMode(r\Objects[19], 2)
						ScaleSprite(r\Objects[19], MeshWidth(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL]) * Scale * 0.95 * 0.5, MeshHeight(mon_I\MonitorModelID[0]) * Scale * 0.95 * 0.5)
						EntityTexture(r\Objects[19], r\Textures[1], 6)
						EntityParent(r\Objects[19], r\Objects[i])
					EndIf
					HideEntity(r\Objects[i])
				Else
					r\Objects[7] = CreatePivot()
					PositionEntity(r\Objects[7], r\x, r\y + 100.0 * RoomScale, r\z - 800.0 * RoomScale, True)
					EntityParent(r\Objects[7], r\OBJ)
				EndIf
			Next
			
			For i = 0 To 2
				PositionEntity(r\Objects[i], r\x - 207.94 * RoomScale, r\y + (648.0 + (112.0 * i)) * RoomScale, r\z - 60.0686 * RoomScale)
				RotateEntity(r\Objects[i], 0.0, 105.0 + r\Angle, 0.0)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For i = 3 To 5
				PositionEntity(r\Objects[i], r\x - 231.489 * RoomScale, r\y + (648.0 + (112.0 * (i - 3))) * RoomScale, r\z + 95.7443 * RoomScale)
				RotateEntity(r\Objects[i], 0.0, 90.0 + r\Angle, 0.0)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For i = 6 To 8 Step 2
				PositionEntity(r\Objects[i], r\x - 231.489 * RoomScale, r\y + (648.0 + (112.0 * (i - 6))) * RoomScale, r\z + 255.744 * RoomScale)
				RotateEntity(r\Objects[i], 0.0, 90.0 + r\Angle, 0.0)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For i = 9 To 11
				PositionEntity(r\Objects[i], r\x - 231.489 * RoomScale, r\y + (648.0 + (112.0 * (i - 9))) * RoomScale, r\z + 415.744 * RoomScale)
				RotateEntity(r\Objects[i], 0.0, 90.0 + r\Angle, 0.0)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For i = 12 To 14
				PositionEntity(r\Objects[i], r\x - 208.138 * RoomScale, r\y + (648.0 + (112.0 * (i - 12))) * RoomScale, r\z + 571.583 * RoomScale)
				RotateEntity(r\Objects[i], 0.0, 75.0 + r\Angle, 0.0)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			; ~ PathPoints for SCP-049
			r\Objects[15] = CreatePivot()
			PositionEntity(r\Objects[15], r\x + 700.0 * RoomScale, r\y + 700.0 * RoomScale, r\z + 256.0 * RoomScale)
			EntityParent(r\Objects[15], r\OBJ)
			
			r\Objects[16] = CreatePivot()
			PositionEntity(r\Objects[16], r\x - 60.0 * RoomScale, r\y + 700.0 * RoomScale, r\z + 200.0 * RoomScale)
			EntityParent(r\Objects[16], r\OBJ)
			
			r\Objects[17] = CreatePivot()
			PositionEntity(r\Objects[17], r\x - 48.0 * RoomScale, r\y + 540.0 * RoomScale, r\z + 656.0 * RoomScale)
			EntityParent(r\Objects[17], r\OBJ)
			
			r\Objects[20] = CreateRedLight(r\x + 958.5 * RoomScale, r\y + 762.5 * RoomScale, r\z + 669.0 * RoomScale)
			r\ScriptedObject[20] = True
			EntityParent(r\Objects[20], r\OBJ)
			HideEntity(r\Objects[20])
			
			; ~ Camera in the room itself
			sc.SecurityCams = CreateSecurityCam(r, r\x - 159.0 * RoomScale, r\y + 384.0 * RoomScale, r\z - 929.0 * RoomScale, 20.0, True, r\x - 231.0 * RoomScale, r\y + 760.0 * RoomScale, r\z + 256.0 * RoomScale, 0.0, 90.0, 0.0)
			sc\Angle = 315.0
			;[End Block]
		Case r_room2_storage
			;[Block]
			d.Doors = CreateDoor(r, r\x - 1288.0 * RoomScale, r\y, r\z, 270.0)
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x - 760.0 * RoomScale, r\y, r\z, 270.0)
			d\MTFClose = False d\DisableWaypoint = True
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			r\RoomDoors.Doors[1] = d
			
			d.Doors = CreateDoor(r, r\x - 264.0 * RoomScale, r\y, r\z, 270.0)
			r\RoomDoors.Doors[2] = d
			
			d.Doors = CreateDoor(r, r\x + 264.0 * RoomScale, r\y, r\z, 270.0)
			r\RoomDoors.Doors[3] = d
			
			d.Doors = CreateDoor(r, r\x + 760.0 * RoomScale, r\y, r\z, 90.0)
			d\MTFClose = False : d\DisableWaypoint = True
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			r\RoomDoors.Doors[4] = d
			
			d.Doors = CreateDoor(r, r\x + 1288.0 * RoomScale, r\y, r\z, 90.0)
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			r\RoomDoors.Doors[5] = d
			
			For i = 2 To 3
				MoveEntity(r\RoomDoors[i]\Buttons[0], 0.0, 0.0, -8.0)
			Next
			
			For i = 0 To 4 Step 2
				r\RoomDoors[i]\AutoClose = False
				MoveEntity(r\RoomDoors[i]\Buttons[1], 0.0, 0.0, -8.0)
				r\RoomDoors[i + 1]\AutoClose = False
				MoveEntity(r\RoomDoors[i + 1]\Buttons[1], 0.0, 0.0, -8.0)
			Next
			
;			it.Items = CreateItem("Document SCP-939", "paper", r\x + 352.0 * RoomScale, r\y + 176.0 * RoomScale, r\z + 256.0 * RoomScale)
;			RotateEntity(it\Collider, 0.0, r\Angle + 4.0, 0.0)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateRandomBattery(r\x + 352.0 * RoomScale, r\y + 112.0 * RoomScale, r\z + 448.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Empty Cup", "emptycup", r\x - 672.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 288.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Level 0 Key Card", "key0", r\x - 672.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 224.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_tesla_lcz, r_room2_tesla_hcz, r_room2_tesla_ez
			;[Block]
			; ~ Tesla gate control door
			d.Doors = CreateDoor(r, r\x - 288.0 * RoomScale, r\y, r\z + 576.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_4)
			r\RoomDoors.Doors[0] = d
			
			r\RoomLevers[0] = CreateLever(r, r\x - 367.0 * RoomScale, r\y + 192.0 * RoomScale, r\z - 212.0 * RoomScale, -90.0, True)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x, r\y + 704.0 * RoomScale, r\z - 953.0 * RoomScale, 30.0, True, r\x - 390.0 * RoomScale, r\y + 204.0 * RoomScale, r\z + 34.0 * RoomScale, 0.0, -90.0, 0.0)
			sc\Turn = 0.0
			
			r\Objects[0] = CreateSprite()
			r\ScriptedObject[0] = True
			EntityTexture(r\Objects[0], t\OverlayTextureID[Orl_Txt_Tesla])
			SpriteViewMode(r\Objects[0], 2) 
			EntityBlend(r\Objects[0], 3) 
			EntityFX(r\Objects[0], 1 + 8 + 16)
			PositionEntity(r\Objects[0], r\x, r\y + 0.8, r\z)
			EntityParent(r\Objects[0], r\OBJ)
			HideEntity(r\Objects[0])
			
			r\Objects[1] = CreateRedLight(r\x - 32.0 * RoomScale, r\y + 568.0 * RoomScale, r\z)
			r\ScriptedObject[1] = True
			EntityParent(r\Objects[1], r\OBJ)
			HideEntity(r\Objects[1])
			
			CreateCustomCenter(r, r\x, r\z - 256.0 * RoomScale)
			;[End Block]
		Case r_room2_test_lcz
			;[Block]
			; ~ Doors
			d.Doors = CreateDoor(r, r\x - 256.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_1)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x - 1024.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, 270.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 512.0 * RoomScale, r\y, r\z + 376.0 * RoomScale, 0.0)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 640.0 * RoomScale, r\y + 0.5, r\z - 912.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 669.0 * RoomScale, r\y + 0.5, r\z - 16.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			; ~ Glass panel
			r\Objects[2] = CreateSprite()
			r\ScriptedObject[2] = True
			Tex = LoadTexture_Strict("GFX\Map\Textures\glass.png", 1 + 2)
			EntityTexture(r\Objects[2], Tex)
			DeleteSingleTextureEntryFromCache(Tex)
			SpriteViewMode(r\Objects[2], 2)
			ScaleSprite(r\Objects[2], 182.0 * RoomScale * 0.5, 190.0 * RoomScale * 0.5)
			PositionEntity(r\Objects[2], r\x - 640 * RoomScale, r\y + 224.0 * RoomScale, r\z - 208.0 * RoomScale)
			TurnEntity(r\Objects[2], 0.0, 180.0, 0.0)
			EntityParent(r\Objects[2], r\OBJ)
			HideEntity(r\Objects[2])
			
			it.Items = CreateItem("Level 2 Key Card", "key2", r\x - 342.0 * RoomScale, r\y + 264.0 * RoomScale, r\z + 102.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 180.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("S-NAV Navigator", "nav", r\x - 930.0 * RoomScale, r\y + 137.0 * RoomScale, r\z + 190.0 * RoomScale)
			it\State = Rnd(100.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_cont2_012
			;[Block]
			; ~ Observation room door
			d.Doors = CreateDoor(r, r\x + 256.0 * RoomScale, r\y, r\z + 672.0 * RoomScale, 270.0, False, DEFAULT_DOOR, KEY_CARD_3)
			
			; ~ SCP-012 chamber door
			d.Doors = CreateDoor(r, r\x - 512.0 * RoomScale, r\y - 768.0 * RoomScale, r\z - 320.0 * RoomScale, 0.0)
			d\MTFClose = False
			PositionEntity(d\Buttons[0], r\x + 176.0 * RoomScale, r\y - 562.0 * RoomScale, r\z - 354.0 * RoomScale, True)
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			r\RoomDoors.Doors[0] = d
			
			r\RoomLevers.Levers[0] = CreateLever(r, r\x + 240.0 * RoomScale, r\y - 584.0 * RoomScale, r\z - 367.0 * RoomScale)
			
			r\Objects[0] = LoadRMesh("GFX\Map\Rooms\cont2_012_box.rmesh", Null, False)
			ScaleEntity(r\Objects[0], RoomScale, RoomScale, RoomScale)
			PositionEntity(r\Objects[0], r\x - 360.0 * RoomScale, r\y - 130.0 * RoomScale, r\z + 456.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreateRedLight(r\x - 43.5 * RoomScale, r\y - 574.0 * RoomScale, r\z - 362.0 * RoomScale)
			r\ScriptedObject[1] = True
			EntityParent(r\Objects[1], r\OBJ)
			HideEntity(r\Objects[1])
			
			r\Objects[2] = LoadRMesh("GFX\Map\Rooms\ventilation_fan.rmesh", Null, False)
			ScaleEntity(r\Objects[2], RoomScale, RoomScale, RoomScale)
			PositionEntity(r\Objects[2], r\x - 450.0 * RoomScale, r\y + 528.0 * RoomScale, r\z - 382.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			r\Objects[3] = LoadMesh_Strict("GFX\Map\Props\scp_012.b3d")
			ScaleEntity(r\Objects[3], RoomScale, RoomScale, RoomScale)
			PositionEntity(r\Objects[3], r\x - 360.0 * RoomScale, r\y - 180.0 * RoomScale, r\z + 456.0 * RoomScale)
			EntityParent(r\Objects[3], r\Objects[0])
			
;			it.Items = CreateItem("Document SCP-012", "paper", r\x - 56.0 * RoomScale, r\y - 576.0 * RoomScale, r\z - 408.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("White Severed Hand", "hand", r\x - 784.0 * RoomScale, r\y - 576.0 * RoomScale + 0.3, r\z + 640.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			de.Decals = CreateDecal(DECAL_BLOOD_2, r\x - 784.0 * RoomScale, r\y - 768.0 * RoomScale + 0.01, r\z + 640.0 * RoomScale, 90.0, Rnd(360.0), 0.0, 0.5)
			EntityParent(de\OBJ, r\OBJ)
			;[End Block]
		Case r_cont2_427_714_860_1025
			;[Block]
			; ~ SCP-860/SCP-1025 door
			d.Doors = CreateDoor(r, r\x + 272.0 * RoomScale, r\y, r\z, 90.0, False, DEFAULT_DOOR, KEY_CARD_3)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) + 0.061, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.061, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			; ~ SCP-427/SCP-714 door
			d.Doors = CreateDoor(r, r\x - 272.0 * RoomScale, r\y, r\z, 270.0, True, DEFAULT_DOOR, KEY_CARD_3)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.061, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.061, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			; ~ SCP-714 Door
			d.Doors = CreateDoor(r, r\x - 560.0 * RoomScale, r\y, r\z - 272.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_3)
			
			; ~ SCP-427 Door
			d.Doors = CreateDoor(r, r\x - 560.0 * RoomScale, r\y, r\z + 272.0 * RoomScale, 0.0, True, DEFAULT_DOOR, KEY_CARD_3)
			
			; ~ SCP-1025 Door
			d.Doors = CreateDoor(r, r\x + 560.0 * RoomScale, r\y, r\z - 272.0 * RoomScale, 180.0, False, DEFAULT_DOOR, KEY_CARD_3)
			
			; ~ SCP-860 Door
			d.Doors = CreateDoor(r, r\x + 560.0 * RoomScale, r\y, r\z + 272.0 * RoomScale, 180.0, False, DEFAULT_DOOR, KEY_CARD_3)
			
			; ~ Misc. doors
			d.Doors = CreateDoor(r, r\x - 816.0 * RoomScale, r\y, r\z, 270.0, False, DEFAULT_DOOR, KEY_CARD_3)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x + 816.0 * RoomScale, r\y, r\z, 90.0, False, DEFAULT_DOOR, KEY_CARD_3)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			For i = 0 To 3
				Select(i)
					Case 0
						;[Block]
						xTemp = 560.0
						zTemp = -416.0
						;[End Block]
					Case 1
						;[Block]
						xTemp = -560.0
						zTemp = -416.0
						;[End Block]
					Case 2
						;[Block]
						xTemp = 560.0
						zTemp = 416.0
						;[End Block]
					Case 3
						;[Block]
						xTemp = -560.0
						zTemp = 416.0
						;[End Block]
				End Select
				sc.SecurityCams = CreateSecurityCam(r, r\x + xTemp * RoomScale, r\y + 386.0 * RoomScale, r\z + zTemp * RoomScale, 30.0)
				If i < 2
					sc\Angle = 180.0
				Else
					sc\Angle = 0.0
				EndIf
				sc\Turn = 30.0
			Next
			
			For i = 0 To 14
				Select(i)
					Case 0
						;[Block]
						xTemp = -64.0
						zTemp = -516.0
						;[End Block]
					Case 1
						;[Block]
						xTemp = -96.0
						zTemp = -388.0
						;[End Block]
					Case 2
						;[Block]
						xTemp = -128.0
						zTemp = -292.0
						;[End Block]
					Case 3
						;[Block]
						xTemp = -128.0
						zTemp = -132.0
						;[End Block]
					Case 4
						;[Block]
						xTemp = -160.0
						zTemp = -36.0
						;[End Block]
					Case 5
						;[Block]
						xTemp = -192.0
						zTemp = 28.0
						;[End Block]
					Case 6
						;[Block]
						xTemp = -384.0
						zTemp = 28.0
						;[End Block]
					Case 7
						;[Block]
						xTemp = -448.0
						zTemp = 92.0
						;[End Block]
					Case 8
						;[Block]
						xTemp = -480.0
						zTemp = 124.0
						;[End Block]
					Case 9
						;[Block]
						xTemp = -512.0
						zTemp = 156.0
						;[End Block]
					Case 10
						;[Block]
						xTemp = -544.0
						zTemp = 220.0
						;[End Block]
					Case 11
						;[Block]
						xTemp = -544.0
						zTemp = 380.0
						;[End Block]
					Case 12
						;[Block]
						xTemp = -544.0
						zTemp = 476.0
						;[End Block]
					Case 13
						;[Block]
						xTemp = -544.0
						zTemp = 572.0
						;[End Block]
					Case 14
						;[Block]
						xTemp = -544.0
						zTemp = 636.0
						;[End Block]
				End Select
				de.Decals = CreateDecal(Rand(DECAL_BLOOD_DROP_1, DECAL_BLOOD_DROP_2), r\x + xTemp * RoomScale, r\y + 0.005, r\z + zTemp * RoomScale, 90.0, Rnd(360.0), 0.0, ((i <= 10) * Rnd(0.2, 0.25)) + ((i > 10) * Rnd(0.1, 0.17)))
				EntityParent(de\OBJ, r\OBJ)
			Next
			
			it.Items = CreateItem("SCP-714", "scp714", r\x - 560.0 * RoomScale, r\y + 185.0 * RoomScale, r\z - 760.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("SCP-1025", "scp1025", r\x + 560.0 * RoomScale, r\y + 185.0 * RoomScale, r\z - 760.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("SCP-860", "scp860", r\x + 560.0 * RoomScale, r\y + 185.0 * RoomScale, r\z + 765.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Document SCP-427", "paper", r\x - 608.0 * RoomScale, r\y + 185.0 * RoomScale, r\z + 636.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
;			
;			it.Items = CreateItem("Document SCP-714", "paper", r\x - 728.0 * RoomScale, r\y + 290.0 * RoomScale, r\z - 360.0 * RoomScale)
;			RotateEntity(it\Collider, 0.0, r\Angle, 0.0)
;			EntityParent(it\Collider, r\OBJ)
;			
;			it.Items = CreateItem("Document SCP-860", "paper", r\x + 728.0 * RoomScale, r\y + 290.0 * RoomScale, r\z + 360.0 * RoomScale)
;			RotateEntity(it\Collider, 0.0, r\Angle, 0.0)
;			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_cont2_500_1499
			;[Block]
			; ~ Containment Chamber 5 Door
			d.Doors = CreateDoor(r, r\x + 288.0 * RoomScale, r\y, r\z + 576.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_4)
			d\Locked = 1
			r\RoomDoors.Doors[0] = d
			
			; ~ SCP-500 door
			d.Doors = CreateDoor(r, r\x + 784.0 * RoomScale, r\y, r\z + 672.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_4)
			
			; ~ SCP-1499 door
			d.Doors = CreateDoor(r, r\x + 556.0 * RoomScale, r\y, r\z + 288.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_4)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 576.0 * RoomScale, r\y + 160.0 * RoomScale, r\z + 632.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			For i = 0 To 1
				Select(i)
					Case 0
						;[Block]
						xTemp = 850.0
						yTemp = 385.0
						zTemp = 876.0
						;[End Block]
					Case 1
						;[Block]
						xTemp = 616.0
						yTemp = 512.0
						zTemp = 150.0
						;[End Block]
				End Select
				sc.SecurityCams = CreateSecurityCam(r, r\x + xTemp * RoomScale, r\y + yTemp * RoomScale, r\z + zTemp * RoomScale, 30.0)
				If i = 0
					sc\Angle = 220.0
				Else
					sc\Angle = 180.0
				EndIf
				sc\Turn = 30.0
			Next
			
;			it.Items = CreateItem("SCP-1499", "scp1499", r\x + 616.0 * RoomScale, r\y + 176.0 * RoomScale, r\z - 234.0 * RoomScale)
;			RotateEntity(it\Collider, 0.0, r\Angle, 0.0)
;			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Document SCP-1499", "paper", r\x + 837.0 * RoomScale, r\y + 260.0 * RoomScale, r\z + 211.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Emily Ross' Badge", "badge", r\x + 364.0 * RoomScale, r\y + 5.0 * RoomScale, r\z + 716.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Document SCP-500", "paper", r\x + 891.0 * RoomScale, r\y + 228.0 * RoomScale, r\z + 485.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ) : RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			
			If Rand(4) = 1
				it.Items = CreateItem("SCP-500", "scp500", r\x + 1147.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 345.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ) : RotateEntity(it\Collider, 0.0, 180.0, 0.0)
			EndIf
			;[End Block]
		Case r_cont2_1123
			;[Block]
			; ~ Fake observation room door
			d.Doors = CreateDoor(r, r\x + 352.0 * RoomScale, r\y + 769.0 * RoomScale, r\z - 640.0 * RoomScale, 90.0)
			d\AutoClose = False : d\DisableWaypoint = True
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			r\RoomDoors.Doors[0] = d
			
			; ~ A door inside the cell 
			d.Doors = CreateDoor(r, r\x - 336.0 * RoomScale, r\y + 769.0 * RoomScale, r\z + 712.0 * RoomScale, 90.0, False, WOODEN_DOOR)
			d\Locked = 2 : d\MTFClose = False
			r\RoomDoors.Doors[1] = d
			
			; ~ An intermediate door
			d.Doors = CreateDoor(r, r\x - 336.0 * RoomScale, r\y + 769.0 * RoomScale, r\z + 168.0 * RoomScale, 270.0, False, WOODEN_DOOR)
			r\RoomDoors.Doors[2] = d
			
			; ~ A door leading to the nazi before shot
			d.Doors = CreateDoor(r, r\x - 668.0 * RoomScale, r\y + 769.0 * RoomScale, r\z - 704.0 * RoomScale, 0.0, False, WOODEN_DOOR)
			r\RoomDoors.Doors[3] = d
			
			; ~ SCP-1123 Chamber door
			d.Doors = CreateDoor(r, r\x + 912.0 * RoomScale, r\y, r\z + 368.0 * RoomScale, 0.0, False, ONE_SIDED_DOOR, KEY_CARD_2)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.06, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.12, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.061, True)
			
			; ~ Observation room door
			d.Doors = CreateDoor(r, r\x + 352.0 * RoomScale, r\y, r\z - 640.0 * RoomScale, 90.0)
			
			; ~ Fake SCP-1123 Chamber door
			d.Doors = CreateDoor(r, r\x + 912.0 * RoomScale, r\y + 769.0 * RoomScale, r\z + 368.0 * RoomScale, 0.0, True, ONE_SIDED_DOOR, KEY_CARD_2)
			d\Locked = 1 : d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.12, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.12, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.061, True)
			
			d.Doors = CreateDoor(r, r\x, r\y + 769.0 * RoomScale, r\z + 416.0 * RoomScale, 0.0, False, WOODEN_DOOR)
			d\Locked = 2 : d\MTFClose = False
			
			d.Doors = CreateDoor(r, r\x, r\y + 769.0 * RoomScale, r\z - 945.0 * RoomScale, 0.0, False, WOODEN_DOOR)
			d\Locked = 2 : d\MTFClose = False
			
			; ~ SCP-1123 sound position
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 912.0 * RoomScale, r\y + 170.0 * RoomScale, r\z + 857.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			; ~ Nazi position near the player's cell
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 139.0 * RoomScale, r\y + 910.0 * RoomScale, r\z + 655.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			; ~ Player's position inside the cell
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 818.0 * RoomScale, r\y + 850.0 * RoomScale, r\z + 736.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			; ~ Player's position after leaving the cell
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 828.0 * RoomScale, r\y + 850.0 * RoomScale, r\z + 592.0 * RoomScale)
			EntityParent(r\Objects[3], r\OBJ)
			
			; ~ Nazi position before the shooting
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x - 706.0 * RoomScale, r\y + 910.0 * RoomScale, r\z - 845.0 * RoomScale)
			EntityParent(r\Objects[4], r\OBJ)
			
			; ~ Nazi position while start shooting
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x - 575.0 * RoomScale, r\y + 910.0 * RoomScale, r\z - 402.0 * RoomScale)
			EntityParent(r\Objects[5], r\OBJ)
			
			; ~ Player's position befor the shooting
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x - 468.0 * RoomScale, r\y + 850.0 * RoomScale, r\z - 273.0 * RoomScale)
			EntityParent(r\Objects[6], r\OBJ)
			
			de.Decals = CreateDecal(DECAL_BLOOD_5, r\x - 550.0 * RoomScale, r\y + 769.0 * RoomScale + 0.005, r\z + 568.0 * RoomScale, 90.0, Rnd(360.0), 0.0, Rnd(0.4, 0.5), Rnd(0.8, 1.0))
			EntityParent(de\OBJ, r\OBJ)
			
			de.Decals = CreateDecal(DECAL_BLOOD_3, r\x + 180.0 * RoomScale, r\y + 769.0 * RoomScale + 0.005, r\z + 797.0 * RoomScale, 90.0, Rnd(360.0), 0.0, Rnd(0.6, 0.7), Rnd(0.8, 1.0))
			EntityParent(de\OBJ, r\OBJ)
			
;			it.Items = CreateItem("Document SCP-1123", "paper", r\x + 606.0 * RoomScale, r\y + 125.0 * RoomScale, r\z - 936.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Gas Mask", "gasmask", r\x + 609.0 * RoomScale, r\y + 150.0 * RoomScale, r\z + 961.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("SCP-1123", "scp1123", r\x + 912.0 * RoomScale, r\y + 170.0 * RoomScale, r\z + 857.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Leaflet", "paper", r\x - 756.0 * RoomScale, r\y + 920.0 * RoomScale, r\z + 521.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2c_lcz
			;[Block]
			d.Doors = CreateDoor(r, r\x + 256.0 * RoomScale, r\y, r\z - 576.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_3)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.165, True)
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			;[End Block]
		Case r_room2c_gw_lcz
			;[Block]
			; ~ Doors
			d.Doors = CreateDoor(r, r\x + 815.0 * RoomScale, r\y, r\z - 352.0 * RoomScale, 0.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.07, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x + 352.0 * RoomScale, r\y, r\z - 815.0 * RoomScale, 90.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.07, True)
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 736.0 * RoomScale, r\y, r\z - 80.0 * RoomScale, 0.0)
			d\AutoClose = False : d\Timer = 70.0 * 5.0
			PositionEntity(d\Buttons[0], r\x - 288.0 * RoomScale, EntityY(d\Buttons[0], True), r\z - 632.0 * RoomScale, True)
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x + 80.0 * RoomScale, r\y, r\z + 736.0 * RoomScale, 270.0)
			d\AutoClose = False : d\Timer = 70.0 * 5.0
			PositionEntity(d\Buttons[0], r\x + 632.0 * RoomScale, EntityY(d\Buttons[0], True), r\z + 288.0 * RoomScale, True)
			RotateEntity(d\Buttons[0], 0.0, 90.0, 0.0, True)
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			r\RoomDoors.Doors[1] = d
			
			r\RoomDoors[0]\LinkedDoor = r\RoomDoors[1]
			r\RoomDoors[1]\LinkedDoor = r\RoomDoors[0]
			
			; ~ Security camera inside
			sc.SecurityCams = CreateSecurityCam(r, r\x - 688.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 688.0 * RoomScale, 40.0, True, r\x + 670.0 * RoomScale, r\y + 280.0 * RoomScale, r\z - 96.0 * RoomScale, 0.0, 90.0, 0.0)
			sc\Angle = 225.0 : sc\Turn = 45.0
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 112.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 112.0 * RoomScale, 40.0, True, r\x + 96.0 * RoomScale, r\y + 280.0 * RoomScale, r\z - 670.0 * RoomScale)
			sc\Angle = 45.0 : sc\Turn = 45.0
			
			; ~ Smoke
			em.Emitters = CreateEmitter(r, r\x - 175.0 * RoomScale, r\y + 370.0 * RoomScale, r\z + 656.0 * RoomScale, 0)
			em\RandAngle = 20.0 : em\Speed = 0.05 : em\SizeChange = 0.007 : em\AlphaChange = -0.006 : em\Gravity = -0.24
			TurnEntity(em\OBJ, 90.0, 0.0, 0.0)
			
			em.Emitters = CreateEmitter(r, r\x - 655.0 * RoomScale, r\y + 370.0 * RoomScale, r\z + 240.0 * RoomScale, 0)
			em\RandAngle = 20.0 : em\Speed = 0.05 : em\SizeChange = 0.007 : em\AlphaChange = -0.006 : em\Gravity = -0.24
			TurnEntity(em\OBJ, 90.0, 0.0, 0.0)
			
			CreateCustomCenter(r, r\x - 736.0 * RoomScale, r\z - 352.0 * RoomScale)
			;[End Block]
		Case r_room2c_gw_2_lcz
			;[Block]
			; ~ Doors
			d.Doors = CreateDoor(r, r\x + 815.0 * RoomScale, r\y, r\z - 352.0 * RoomScale, 180.0, True)
			d\Locked = 1 : d\MTFClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) + 0.07, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.07, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x + 352.0 * RoomScale, r\y, r\z - 815.0 * RoomScale, 90.0, True)
			d\Locked = 1 : d\MTFClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) - 0.07, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.07, True)
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 736.0 * RoomScale, r\y, r\z - 80.0 * RoomScale, 0.0)
			d\Locked = 1
			PositionEntity(d\Buttons[0], r\x - 288.0 * RoomScale, EntityY(d\Buttons[0], True), r\z - 632.0 * RoomScale, True)
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			d2.Doors = CreateDoor(r, r\x + 80.0 * RoomScale, r\y, r\z + 736.0 * RoomScale, 270.0)
			d2\Locked = 1
			PositionEntity(d2\Buttons[0], r\x + 632.0 * RoomScale, EntityY(d2\Buttons[0], True), r\z + 288.0 * RoomScale, True)
			RotateEntity(d2\Buttons[0], 0.0, 90.0, 0.0, True)
			FreeEntity(d2\Buttons[1]) : d2\Buttons[1] = 0
			
			d\LinkedDoor = d2
			d2\LinkedDoor = d
			
			CreateCustomCenter(r, r\x + 815.0 * RoomScale, r\z - 815.0 * RoomScale)
			;[End Block]
		Case r_cont2c_066_1162_arc
			;[Block]
			; SCP-1162-arc chamber door
			d.Doors = CreateDoor(r, r\x + 248.0 * RoomScale, r\y, r\z - 736.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_2)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.031, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.031, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			; SCP-066's chamber door
			d.Doors = CreateDoor(r, r\x - 288.0 * RoomScale, r\y, r\z, 90.0, True, DEFAULT_DOOR, KEY_CARD_3)
            PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) - 0.132, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.032, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 996.0 * RoomScale, r\y + 128.0 * RoomScale, r\z - 640.0 * RoomScale)
			EntityPickMode(r\Objects[0], 1)
			EntityParent(r\Objects[0], r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 192.0 * RoomScale, r\y + 704.0 * RoomScale, r\z + 192.0 * RoomScale, 20.0)
			sc\Angle = 225.0 : sc\Turn = 45.0
			
;			it.Items = CreateItem("Document SCP-1162-ARC", "paper", r\x + 752.227 * RoomScale, r\y + 152.0 * RoomScale, r\z - 299.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
;			
;			it.Items = CreateItem("Document SCP-066", "paper", r\x + 340.0 * RoomScale, r\y + 152.0 * RoomScale, r\z - 235.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
;			RotateEntity(it\Collider, 0.0, 240.0, 0.0)
			
			it.Items = CreateItem("S-NAV Navigator", "nav", r\x - 657.0 * RoomScale, r\y + 152.0 * RoomScale, r\z + 50.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room3_storage
			;[Block]
			; ~ Elevator Doors
			d.Doors = CreateDoor(r, r\x, r\y, r\z + 448.0 * RoomScale, 0.0, True, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x + 5840.0 * RoomScale, r\y - 5632.0 * RoomScale, r\z + 1040.0 * RoomScale, 0.0, False, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[1] = d
			
			d.Doors = CreateDoor(r, r\x + 608.0 * RoomScale, r\y, r\z - 306.0 * RoomScale, 180.0, True, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[2] = d
			
			d.Doors = CreateDoor(r, r\x - 456.0 * RoomScale, r\y - 5632.0 * RoomScale, r\z - 816.0 * RoomScale, 180.0, False, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[3] = d
			
			; ~ Remote opening door
			d.Doors = CreateDoor(r, r\x + 56.0 * RoomScale, r\y - 5632.0 * RoomScale, r\z + 6300.0 * RoomScale, 90.0, False, HEAVY_DOOR)
			d\AutoClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[4] = d
			
			; ~ Misc doors
			
			; ~ Storage Area 3E Door
			d.Doors = CreateDoor(r, r\x + 1083.0 * RoomScale, r\y - 5632.0 * RoomScale, r\z + 660.0 * RoomScale, 0.0, False, HEAVY_DOOR)
			d\AutoClose = False : d\Locked = 1
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			
			; ~ Storage Area 3C Door
			d.Doors = CreateDoor(r, r\x + 3446.0 * RoomScale, r\y - 5632.0 * RoomScale, r\z + 6300.0 * RoomScale, 90.0, False, HEAVY_DOOR)
			d\AutoClose = False : d\Locked = 1
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			
			; ~ DNA door
			d.Doors = CreateDoor(r, r\x + 256.0 * RoomScale, r\y, r\z, 90.0, False, DEFAULT_DOOR, KEY_HAND_YELLOW)
			
			r\RoomLevers.Levers[0] = CreateLever(r, r\x + 3096.0 * RoomScale, r\y - 5464.0 * RoomScale, r\z + 6569.0 * RoomScale)
			r\RoomLevers.Levers[1] = CreateLever(r, r\x + 1216.0 * RoomScale, r\y - 5464.0 * RoomScale, r\z + 3240.0 * RoomScale)
			
			; ~ Elevators' pivots
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x, r\y + 240.0 * RoomScale, r\z + 752.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 5840.0 * RoomScale, r\y - 5392.0 * RoomScale, r\z + 1344.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x + 608.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 610.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x - 456.0 * RoomScale, r\y - 5392.0 * RoomScale, r\z - 1120.0 * RoomScale)
			EntityParent(r\Objects[3], r\OBJ)
			
			; ~ Waypoints # 1
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x + 2155.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 1966.0 * RoomScale)
			EntityParent(r\Objects[4], r\OBJ)
			
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x + 2155.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z - 968.0 * RoomScale)
			EntityParent(r\Objects[5], r\OBJ)
			
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x + 3980.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z - 968.0 * RoomScale)
			EntityParent(r\Objects[6], r\OBJ)
			
			r\Objects[7] = CreatePivot()
			PositionEntity(r\Objects[7], r\x + 3980.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 1966.0 * RoomScale)
			EntityParent(r\Objects[7], r\OBJ)
			
			; ~ Waypoints # 2
			r\Objects[8] = CreatePivot()
			PositionEntity(r\Objects[8], r\x + 567.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 5176.0 * RoomScale)
			EntityParent(r\Objects[8], r\OBJ)
			
			r\Objects[9] = CreatePivot()
			PositionEntity(r\Objects[9], r\x + 567.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 6373.0 * RoomScale)
			EntityParent(r\Objects[9], r\OBJ)
			
			r\Objects[10] = CreatePivot()
			PositionEntity(r\Objects[10], r\x + 2940.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 6373.0 * RoomScale)
			EntityParent(r\Objects[10], r\OBJ)
			
			r\Objects[11] = CreatePivot()
			PositionEntity(r\Objects[11], r\x + 2940.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 5176.0 * RoomScale)
			EntityParent(r\Objects[11], r\OBJ)
			
			; ~ Waypoints # 3
			r\Objects[12] = CreatePivot()
			PositionEntity(r\Objects[12], r\x + 1083.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 3023.0 * RoomScale)
			EntityParent(r\Objects[12], r\OBJ)
			
			r\Objects[13] = CreatePivot()
			PositionEntity(r\Objects[13], r\x + 1083.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 1180.0 * RoomScale)
			EntityParent(r\Objects[13], r\OBJ)
			
			r\Objects[14] = CreatePivot()
			PositionEntity(r\Objects[14], r\x - 456.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 1180.0 * RoomScale)
			EntityParent(r\Objects[14], r\OBJ)
			
			r\Objects[15] = CreatePivot()
			PositionEntity(r\Objects[15], r\x - 456.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 3023.0 * RoomScale)
			EntityParent(r\Objects[15], r\OBJ)
			
			em.Emitters = CreateEmitter(r, r\x + 5245.0 * RoomScale, r\y - 5584.0 * RoomScale, r\z - 575.0 * RoomScale, 0)
			em\RandAngle = 15.0 : em\Speed = 0.03 : em\SizeChange = 0.01 : em\AlphaChange = -0.006 : em\Gravity = -0.2 
			TurnEntity(em\OBJ, 20.0, -100.0, 0.0)
			
			Select(Rand(3))
				Case 1
					;[Block]
					xTemp = 4674.0
					zTemp = 950.0
					;[End Block]
				Case 2
					;[Block]
					xTemp = 3032.0
					zTemp = 1288.0
					;[End Block]
				Case 3
					;[Block]
					xTemp = 2938.0
					zTemp = 2793.0
					;[End Block]
			End Select
			
			it.Items = CreateItem("Black Severed Hand", "hand2", r\x + xTemp * RoomScale, r\y - 5496.0 * RoomScale, r\z + zTemp * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Level 1 Key Card", "key1", r\x + 3370.0 * RoomScale, r\y - 5496.0 * RoomScale, r\z + 6201.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Night Vision Goggles", "nvg", r\x + 1248.0 * RoomScale, r\y - 5496.0 * RoomScale, r\z + 981.0 * RoomScale)
			it\State = Rnd(0.0, 1000.0)
			EntityParent(it\Collider, r\OBJ)
			
			de.Decals = CreateDecal(DECAL_BLOOD_2, r\x + xTemp * RoomScale, r\y - 5632.0 * RoomScale + 0.005, r\z + zTemp * RoomScale, 90.0, Rnd(360.0), 0.0, 0.5)
			EntityParent(de\OBJ, r\OBJ)
			
			de.Decals = CreateDecal(DECAL_BLOOD_2, r\x + 3438.0 * RoomScale, r\y - 5510.0 * RoomScale, r\z + 6294.0 * RoomScale, 0.0, r\Angle + 270.0, 0.0, 0.3)
			EntityParent(de\OBJ, r\OBJ)
			
			de.Decals = CreateDecal(DECAL_BLOOD_6, r\x + 1083.0 * RoomScale, r\y - 5632.0 * RoomScale + 0.005, r\z + 890.0 * RoomScale, 90.0, r\Angle + 180.0, 0.0, 0.5)
			EntityParent(de\OBJ, r\OBJ)
			;[End Block]
		Case r_room4_ic
			;[Block]
			; ~ Information Center door
			d.Doors = CreateDoor(r, r\x + 704.0 * RoomScale, r\y, r\z - 336.0 * RoomScale, 0.0, False, OFFICE_DOOR)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x + 320.0 * RoomScale, r\y + 544.0 * RoomScale, r\z - 320.0 * RoomScale, 30.0)
			sc\FollowPlayer = True
			
			r\Objects[0] = CopyEntity(mon_I\MonitorModelID[MONITOR_CHECKPOINT_MODEL], r\OBJ)
			PositionEntity(r\Objects[0], r\x - 700.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 290.0 * RoomScale, True)
			ScaleEntity(r\Objects[0], 2.0, 2.0, 2.0)
			RotateEntity(r\Objects[0], 0.0, 0.0, 0.0)
			
			it.Items = CreateItem("Clipboard", "clipboard", r\x + 919.0 * RoomScale, r\y + 200.0 * RoomScale, r\z - 855.0 * RoomScale)
			; ~ A hacky fix for clipboard's model and icon
			it\InvImg = it\ItemTemplate\InvImg
			SetAnimTime(it\Model, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
;			it2.Items = CreateItem("Document SCP-1048", "paper", 1.0, 1.0, 1.0)
;			it2\Picked = True : it2\Dropped = -1 : it\SecondInv[0] = it2
;			HideEntity(it2\Collider)
;			EntityParent(it2\Collider, r\OBJ)
			;[End Block]
		Case r_room2_checkpoint_lcz_hcz, r_room2_checkpoint_hcz_ez
			;[Block]
			d.Doors= CreateDoor(r, r\x, r\y, r\z - 400.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_3)
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x, r\y, r\z + 400.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_3)
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[1] = d
			
			If CurrMapGrid\Grid[Floor(r\x / RoomSpacing) + ((Floor(r\z / RoomSpacing) - 1) * MapGridSize)] = MapGrid_NoTile
				d.Doors = CreateDoor(r, r\x, r\y, r\z - 1026.0 * RoomScale, 0.0, False, HCZ_DOOR)
				d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
				FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			EndIf
			
			r\Objects[0] = CreateButton(BUTTON_DEFAULT, r\x - 140.524 * RoomScale, r\y + 139.348 * RoomScale, r\z, 45,90,0)
			EntityParent (r\Objects[0],r\OBJ)
			
			; ~ Monitors at the both sides
			r\Objects[1] = CopyEntity(mon_I\MonitorModelID[MONITOR_CHECKPOINT_MODEL], r\OBJ)
			PositionEntity(r\Objects[1], r\x, r\y + 384.0 * RoomScale, r\z + 256.0 * RoomScale, True)
			ScaleEntity(r\Objects[1], 2.0, 2.0, 2.0)
			RotateEntity(r\Objects[1], 0.0, 180.0, 0.0)
			
			r\Objects[2] = CopyEntity(mon_I\MonitorModelID[MONITOR_CHECKPOINT_MODEL], r\OBJ)
			PositionEntity(r\Objects[2], r\x, r\y + 384.0 * RoomScale, r\z - 256.0 * RoomScale, True)
			ScaleEntity(r\Objects[2], 2.0, 2.0, 2.0)
			RotateEntity(r\Objects[2], 0.0, 0.0, 0.0)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 192.0 * RoomScale, r\y + 704.0 * RoomScale, r\z + 960.0 * RoomScale, 20.0)
			sc\Angle = 225.0 : sc\Turn = 0.0
			
			CreateCustomCenter(r, r\x, r\z + 500.0 * RoomScale)
			;[End Block]
		Case r_cont1_035
			;[Block]
			; ~ The doors to the containment chamber of SCP-035
			d.Doors = CreateDoor(r, r\x - 296.0 * RoomScale, r\y, r\z - 672.0 * RoomScale, 180.0, True, ONE_SIDED_DOOR, KEY_CARD_5)
			d\AutoClose = False : d\Locked = 1
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], r\x - 164.0 * RoomScale, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x - 296.0 * RoomScale, r\y, r\z - 144.0 * RoomScale, 0.0, False, ONE_SIDED_DOOR)
			d\AutoClose = False : d\Locked = 1
			PositionEntity(d\Buttons[0], r\x - 438.0 * RoomScale, EntityY(d\Buttons[0], True), r\z - 480.0 * RoomScale, True)
			RotateEntity(d\Buttons[0], 0.0, 90.0, 0.0, True)
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			r\RoomDoors.Doors[1] = d
			
			r\RoomDoors[0]\LinkedDoor = r\RoomDoors[1]
			r\RoomDoors[1]\LinkedDoor = r\RoomDoors[0]
			
			; ~ The door to the control room
			d.Doors = CreateDoor(r, r\x + 384.0 * RoomScale, r\y, r\z - 672.0 * RoomScale, 180.0, False, DEFAULT_DOOR, KEY_CARD_5)
			r\RoomDoors.Doors[2] = d
			
			; ~ The door to the storage room
			d.Doors = CreateDoor(r, r\x + 768.0 * RoomScale, r\y, r\z + 512.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_MISC, Str(CODE_CONT1_035))
			r\RoomDoors.Doors[3] = d
			
			r\RoomLevers.Levers[0] = CreateLever(r, r\x + 209.0 * RoomScale, r\y + 224.0 * RoomScale, r\z - 205.0 * RoomScale, -270.0)
			r\RoomLevers.Levers[1] = CreateLever(r, r\x + 209.0 * RoomScale, r\y + 224.0 * RoomScale, r\z - 132.0 * RoomScale, -270.0)
			
			; ~ The control room
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 456.0 * RoomScale, r\y + 0.5, r\z + 400.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 576.0 * RoomScale, r\y + 0.5, r\z + 640.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			; ~ The corners of the cont chamber (needed to calculate whether the player is inside the chamber)
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 720.0 * RoomScale, r\y + 0.5, r\z + 880.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 176.0 * RoomScale, r\y + 0.5, r\z - 144.0 * RoomScale)
			EntityParent(r\Objects[3], r\OBJ)
			
			r\Objects[4] = LoadMesh_Strict("GFX\Map\Props\cont1_035_label.b3d")
			Update035Label(r\Objects[4])
			ScaleEntity(r\Objects[4], RoomScale, RoomScale, RoomScale)
			PositionEntity(r\Objects[4], r\x - 30.0 * RoomScale, r\y + 230.0 * RoomScale, r\z - 704.0 * RoomScale)
			EntityParent(r\Objects[4], r\OBJ)
			
;			it.Items = CreateItem("SCP-035 Addendum", "paper", r\x + 687.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 127.0 * RoomScale)
;			RotateEntity(it\Collider, 0.0, r\Angle, 0.0)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Radio Transceiver", "radio", r\x - 544.0 * RoomScale, r\y + 0.5, r\z + 704.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("SCP-500-01", "scp500pill", r\x + 1168.0 * RoomScale, r\y + 250.0 * RoomScale, r\z + 576 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Metal Panel", "scp148", r\x - 360.0 * RoomScale, r\y + 0.5, r\z + 644.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Document SCP-035", "paper", r\x + 1168.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 408.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x, r\z - 848.0 * RoomScale)
			;[End Block]
		Case r_cont1_079
			;[Block]
			; ~ A blast door leading to the chamber of SCP-079
			d.Doors = CreateDoor(r, r\x - 1648.0 * RoomScale, r\y - 10688.0 * RoomScale, r\z + 1230.0 * RoomScale, 90.0, False, BIG_DOOR, KEY_CARD_4)
			PositionEntity(d\Buttons[0], r\x - 1894.0 * RoomScale, EntityY(d\Buttons[0], True), r\z + 1675.0 * RoomScale, True)
			RotateEntity(d\Buttons[0], 0.0, EntityYaw(d\Buttons[0], True) + 180.0, 0.0, True)
			PositionEntity(d\Buttons[1], r\x - 1418.0 * RoomScale, EntityY(d\Buttons[1], True), r\z + 1562.0 * RoomScale, True)
			r\RoomDoors.Doors[0] = d
			
			; ~ Elevators' doors
			d.Doors = CreateDoor(r, r\x + 512.0 * RoomScale, r\y, r\z - 256.0 * RoomScale, -90.0, True, MT_ELEVATOR_DOOR)
			PositionEntity(d\ElevatorPanel[1], EntityX(d\ElevatorPanel[1], True), EntityY(d\ElevatorPanel[1], True) + 0.05, EntityZ(d\ElevatorPanel[1], True), True)
			r\RoomDoors.Doors[1] = d
			
			d.Doors = CreateDoor(r, r\x + 512.0 * RoomScale, r\y - 10240.0 * RoomScale, r\z - 256.0 * RoomScale, -90.0, False, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[2] = d
			
			; ~ Observation room door
			d.Doors = CreateDoor(r, r\x - 1202.0 * RoomScale, r\y - 10688.0 * RoomScale, r\z + 872.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_HAND_WHITE)
			
			; ~ Misc Doors
			d.Doors = CreateDoor(r, r\x, r\y, r\z + 64.0 * RoomScale, 0.0, False, HEAVY_DOOR, KEY_CARD_4)
			d\Locked = 1 : d\MTFClose = False : d\DisableWaypoint = True
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			r\Objects[0] = LoadAnimMesh_Strict("GFX\Map\Props\079.b3d")
			r\ScriptedObject[0] = True
			PositionEntity(r\Objects[0], r\x + 166.0 * RoomScale, r\y - 10800.0 * RoomScale, r\z + 1606.0 * RoomScale)
			ScaleEntity(r\Objects[0], 1.3, 1.3, 1.3)
			RotateEntity(r\Objects[0], 0.0, r\Angle - 90.0, 0.0, True)
			EntityParent(r\Objects[0], r\OBJ)
			HideEntity(r\Objects[0])
			
			r\Objects[1] = CreateSprite(r\Objects[0])
			r\ScriptedObject[1] = True
			SpriteViewMode(r\Objects[1], 2)
			PositionEntity(r\Objects[1], 0.082, 0.119, 0.010)
			ScaleSprite(r\Objects[1], 0.09, 0.0725)
			TurnEntity(r\Objects[1], 0.0, 13.0, 0.0)
			MoveEntity(r\Objects[1], 0.0, 0.0, -0.022)
			EntityTexture(r\Objects[1], mon_I\MonitorOverlayID[MONITOR_079_OVERLAY_1])
			HideEntity(r\Objects[1])
			
			; ~ Elevators' pivots
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x + 816.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 256.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 816.0 * RoomScale, r\y - 10000.0 * RoomScale, r\z - 256.0 * RoomScale)
			EntityParent(r\Objects[3], r\OBJ)
			
			CreateCustomCenter(r, r\x, r\z - 256.0 * RoomScale)
			;[End Block]
		Case r_cont1_096
			;[Block]
			; ~ Observation room doors
			d.Doors = CreateDoor(r, r\x - 320.0 * RoomScale, r\y, r\z + 320.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_4)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			d.Doors = CreateDoor(r, r\x, r\y, r\z, 0.0, False, DEFAULT_DOOR, KEY_CARD_4)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			de.Decals = CreateDecal(DECAL_BLOOD_1, r\x - 108.0 * RoomScale, r\y + 4.0 * RoomScale + 0.005, r\z - 544.0 * RoomScale, 90.0, Rnd(360.0), 0.0, 0.8, 0.8)
			EntityParent(de\OBJ, r\OBJ)
			
			de.Decals = CreateDecal(DECAL_CORROSIVE_1, r\x - 384.0 * RoomScale, r\y + 0.005, r\z - 512.0 * RoomScale, 90.0, Rnd(360.0), 0.0, 0.5, 0.5, 1)
			EntityParent(de\OBJ, r\OBJ)
			
;			it.Items = CreateItem("Data Report", "paper", r\x - 152.0 * RoomScale, r\y + 90.0 * RoomScale, r\z - 594.0 * RoomScale)
;			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateRandomBattery(r\x - 514.0 * RoomScale, r\y + 150.0 * RoomScale, r\z + 63.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Document SCP-096", "paper", r\x - 500.0 * RoomScale, r\y + 220.0 * RoomScale, r\z + 63.0 * RoomScale)
;			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("SCRAMBLE Gear", "scramble", r\x - 860.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 80.0 * RoomScale)
			it\State = Rnd(0.0, 1000.0)
			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x, r\z - 512.0 * RoomScale)
			;[End Block]
		Case r_cont1_106
			;[Block]
			; ~ Elevators' doors
			d.Doors = CreateDoor(r, r\x - 704.0 * RoomScale, r\y, r\z - 704.0 * RoomScale, 90.0, True, MT_ELEVATOR_DOOR) 
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x - 704.0 * RoomScale, r\y - 7327.9 * RoomScale, r\z - 704.0 * RoomScale, 90.0, False, MT_ELEVATOR_DOOR) 
			r\RoomDoors.Doors[1] = d
			
			; ~ Door to the containment area
			d.Doors = CreateDoor(r, r\x - 178.0 * RoomScale, r\y - 7328.0 * RoomScale, r\z - 422.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_4)
			
			; ~ Doors to the lower area
			d.Doors = CreateDoor(r, r\x - 1140.0 * RoomScale, r\y - 8100.0 * RoomScale, r\z + 1613.0 * RoomScale, 180.0, False, DEFAULT_DOOR, KEY_CARD_4)
			
			d.Doors = CreateDoor(r, r\x - 762.0 * RoomScale, r\y - 8608.0 * RoomScale, r\z + 51.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_4)
			
			; ~ Misc doors
			d.Doors = CreateDoor(r, r\x + 384.0 * RoomScale, r\y, r\z - 704.0 * RoomScale, 90.0, False, HEAVY_DOOR, KEY_CARD_4)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			
			d.Doors = CreateDoor(r, r\x - 288.0 * RoomScale, r\y - 7328.0 * RoomScale, r\z - 1602.0 * RoomScale, 0.0, False, HEAVY_DOOR) 
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			
			; ~ Levers
			r\RoomLevers.Levers[0] = CreateLever(r, r\x - 744.0 * RoomScale, r\y - 7908.0 * RoomScale, r\z + 3123.0 * RoomScale, 0.0, True)
			r\RoomLevers.Levers[1] = CreateLever(r, r\x - 665.0 * RoomScale, r\y - 7908.0 * RoomScale, r\z + 3123.0 * RoomScale)
			
			; ~ Security camera inside the box
			sc.SecurityCams = CreateSecurityCam(r, r\x + 768.0 * RoomScale, r\y - 5936.0 * RoomScale, r\z + 1632.0 * RoomScale, 45.0, True, r\x - 462.0 * RoomScale, r\y - 7872.0 * RoomScale, r\z + 3105.0 * RoomScale, 0.0, -10.0, 0.0)
			sc\Angle = 315.0 : sc\Turn = 20.0
			sc\ScriptedCamera = True
			HideEntity(sc\BaseOBJ)
			r\RoomSecurityCams[0] = sc
			
			; ~ Security camera inside the observation room
			sc.SecurityCams = CreateSecurityCam(r, r\x - 1439.0 * RoomScale, r\y - 7664.0 * RoomScale, r\z + 1709.0 * RoomScale, 20.0)
			sc\Angle = 315.0 : sc\Turn = 30.0
			
			; ~ Femur breaker button
			r\Objects[0] = CreateButton(BUTTON_DEFAULT, r\x - 337.0 * RoomScale, r\y - 7904.0 * RoomScale, r\z + 3136.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			; ~ Chamber
			r\Objects[1] = LoadRMesh("GFX\Map\Rooms\cont1_106_box.rmesh", Null, False)
			ScaleEntity(r\Objects[1], RoomScale, RoomScale, RoomScale)
			EntityPickMode(r\Objects[1], 2)
			PositionEntity(r\Objects[1], r\x + 692.0 * RoomScale, r\y - 8308.0 * RoomScale, r\z + 1032.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			; ~ Elevators pivots
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 1008.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 704.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x - 1008.0 * RoomScale, r\y - 7088.0 * RoomScale, r\z - 704.0 * RoomScale)
			EntityParent(r\Objects[3], r\OBJ)
			
			; ~ Stairs hitbox
			r\Objects[4] = LoadMesh_Strict("GFX\Map\Rooms\cont1_106_hb.b3d", r\OBJ)
			r\ScriptedObject[4] = True
			EntityPickMode(r\Objects[4], 2)
			EntityAlpha(r\Objects[4], 0.0)
			
			it.Items = CreateItem("Level 5 Key Card", "key5", r\x - 1275.0 * RoomScale, r\y - 7910.0 * RoomScale, r\z + 3106.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Dr. Allok's Note", "paper", r\x - 87.0 * RoomScale, r\y - 7904.0 * RoomScale, r\z + 2535.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
;			
;			it.Items = CreateItem("Recall Protocol RP-106-N", "paper", r\x - 989.0 * RoomScale, r\y - 8008.0 * RoomScale, r\z + 3107.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x - 132.0 * RoomScale, r\z - 704.0 * RoomScale)
			;[End Block]
		Case r_cont1_895
			;[Block]
			; ~ The door to the containment chamber of SCP-895
			d.Doors = CreateDoor(r, r\x, r\y, r\z - 448.0 * RoomScale, 0.0, False, BIG_DOOR, KEY_CARD_2)
			PositionEntity(d\Buttons[0], r\x - 390.0 * RoomScale, EntityY(d\Buttons[i], True), r\z - 280.0 * RoomScale, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.025, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			r\RoomDoors.Doors[0] = d
			
			; ~ Observation room door
			d.Doors = CreateDoor(r, r\x - 417.0 * RoomScale, r\y, r\z, 90.0, True, DEFAULT_DOOR, KEY_CARD_2)
			
			r\RoomLevers.Levers[0] = CreateLever(r, r\x - 800.0 * RoomScale, r\y + 180.0 * RoomScale, r\z - 339.0 * RoomScale, 180.0, True)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x, r\y - 1532.0 * RoomScale, r\z + 2508.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 320.0 * RoomScale, r\y + 704.0 * RoomScale, r\z + 288.0 * RoomScale, 120.06, True, r\x - 800.0 * RoomScale, r\y + 288.0 * RoomScale, r\z - 340.0 * RoomScale, 0.0, 180.0, 0.0)
			sc\Angle = 225.0 : sc\Turn = 45.0 : sc\CoffinEffect = 1 : sc_I\CoffinCam = sc
			
;			it.Items = CreateItem("Document SCP-895", "paper", r\x - 688.0 * RoomScale, r\y + 133.0 * RoomScale, r\z - 304.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Night Vision Goggles", "nvg", r\x + 50.0 * RoomScale, r\y - 1302.0 * RoomScale, r\z + 2415.0 * RoomScale)
			it\State = Rnd(0.0, 1000.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_2_hcz
			;[Block]
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 368.0 * RoomScale, r\y, r\z)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 368.0 * RoomScale, r\y, r\z)
			EntityParent(r\Objects[1], r\OBJ)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x + 224.0 * RoomScale - 0.005, r\y + 192.0 * RoomScale, r\z)
			EntityParent(r\Objects[2], r\OBJ)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x - 224.0 * RoomScale + 0.005, r\y + 192.0 * RoomScale, r\z)
			EntityParent(r\Objects[3], r\OBJ)
			;[End Block]
		Case r_room2_4_hcz
			;[Block]
			d.Doors = CreateDoor(r, r\x + 768.0 * RoomScale, r\y, r\z - 827.5 * RoomScale, 90.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.1, True)
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			; ~ Smoke
			i = 0
			For xTemp = -1 To 1 Step 2
				For zTemp = -1 To 1
					em.Emitters = CreateEmitter(r, r\x + 202.0 * RoomScale * xTemp, r\y + 8.0 * RoomScale, r\z + 256.0 * RoomScale * zTemp, 0)
					em\RandAngle = 30.0 : em\Speed = 0.0045 : em\SizeChange = 0.007 : em\AlphaChange = -0.016
					If i < 3
						TurnEntity(em\OBJ, -45.0, -90.0, 0.0)
					Else
						TurnEntity(em\OBJ, -45.0, 90.0, 0.0)
					EndIf
					i = i + 1
				Next
			Next
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 864.0 * RoomScale, r\y - 477.0 * RoomScale, r\z - 632.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			;[End Block]
		Case r_room2_6_hcz
			;[Block]
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x, r\y + 544.0 * RoomScale, r\z + 512.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x, r\y + 544.0 * RoomScale, r\z - 512.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			;[End Block]
		Case r_room2_mt
			;[Block]
			; ~ Elevators doors
			d.Doors = CreateDoor(r, r\x + 256.0 * RoomScale, r\y, r\z + 656.0 * RoomScale, -90.0, True, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x - 256.0 * RoomScale, r\y, r\z - 656.0 * RoomScale, 90.0, True, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[2] = d
			
			d.Doors = CreateDoor(r, r\x, r\y, r\z, 0.0, False, BIG_DOOR, KEY_MISC, CODE_MAINTENANCE_TUNNELS)
			PositionEntity(d\Buttons[0], r\x + 230.0 * RoomScale, EntityY(d\Buttons[1], True), r\z - 384.0 * RoomScale, True)
			RotateEntity(d\Buttons[0], 0.0, -90.0, 0.0, True)
			PositionEntity(d\Buttons[1], r\x - 230.0 * RoomScale, EntityY(d\Buttons[1], True), r\z + 384.0 * RoomScale, True)
			RotateEntity(d\Buttons[1], 0.0, 90.0, 0.0, True)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 2640.0 * RoomScale, r\y + MTGridY, r\z + 400.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 4336.0 * RoomScale, r\y + MTGridY, r\z - 2512.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			; ~ Elevators pivots
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x + 560.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 656.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x - 560.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 656.0 * RoomScale)
			EntityParent(r\Objects[4], r\OBJ)
			
			de.Decals = CreateDecal(DECAL_CORROSIVE_1, r\x + 64.0 * RoomScale, r\y + 0.005, r\z + 144.0 * RoomScale, 90.0, Rnd(360.0), 0.0)
			EntityParent(de\OBJ, r\OBJ)
			
;			it.Items = CreateItem("Scorched Note", "paper", r\x + 64.0 * RoomScale, r\y + 144.0 * RoomScale, r\z - 384.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x, r\z - 656.0 * RoomScale)
			;[End Block]
		Case r_room2_nuke
			;[Block]
			; ~ Elevators doors
			d.Doors = CreateDoor(r, r\x + 1200.0 * RoomScale, r\y, r\z, -90.0, True, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x + 1200.0 * RoomScale, r\y + 3808.0 * RoomScale, r\z, -90.0, False, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[1] = d
			
			r\RoomLevers.Levers[0] = CreateLever(r, r\x - 497.0 * RoomScale, r\y + 4016.0 * RoomScale, r\z - 553.0 * RoomScale, -270.0, True)
			r\RoomLevers.Levers[1] = CreateLever(r, r\x - 497.0 * RoomScale, r\y + 4016.0 * RoomScale, r\z - 421.0 * RoomScale, -270.0, True)
			
			; ~ Omega Warhead entrance door
			d.Doors = CreateDoor(r, r\x + 576.0 * RoomScale, r\y, r\z + 152.0 * RoomScale, 90.0, False, ONE_SIDED_DOOR, KEY_CARD_5)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) - 0.09, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.09, True)
			
			; ~ Omega Warhead remote controls door
			d.Doors = CreateDoor(r, r\x - 32.0 * RoomScale, r\y + 3808.0 * RoomScale, r\z + 692.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_5)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) - 0.075, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.075, True)
			
			; ~ Misc. door
			d.Doors = CreateDoor(r, r\x - 288.0 * RoomScale, r\y + 3808.0 * RoomScale, r\z + 896.0 * RoomScale, 180.0, False, DEFAULT_DOOR, KEY_CARD_5)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			; ~ Elevators pivots
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 1504.0 * RoomScale, r\y + 240.0 * RoomScale, r\z)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 1504.0 * RoomScale, r\y + 4048.0 * RoomScale, r\z)
			EntityParent(r\Objects[1], r\OBJ)
			
;			it.Items = CreateItem("Nuclear Device Document", "paper", r\x - 464.0 * RoomScale, r\y + 3958.0 * RoomScale, r\z - 710.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Ballistic Vest", "vest", r\x - 248.0 * RoomScale, r\y + 3958.0 * RoomScale, r\z - 818.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, -90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x + 1121.0 * RoomScale, r\y + 4191.0 * RoomScale, r\z - 306.0 * RoomScale, 20.0)
			sc\Angle = 90.0 : sc\Turn = 45.0
			;[End Block]
		Case r_room2_servers_hcz
			;[Block]
			; ~ Generator room doors
			d.Doors = CreateDoor(r, r\x - 224.0 * RoomScale, r\y, r\z - 736.0 * RoomScale, 90.0, True)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x - 224.0 * RoomScale, r\y, r\z + 736.0 * RoomScale, 90.0, True)
			r\RoomDoors.Doors[1] = d
			
			r\RoomLevers.Levers[0] = CreateLever(r, r\x - 1260.0 * RoomScale, r\y + 234.0 * RoomScale, r\z + 753.0 * RoomScale)
			r\RoomLevers.Levers[1] = CreateLever(r, r\x - 917.0 * RoomScale, r\y + 164.0 * RoomScale, r\z + 899.0 * RoomScale)
			r\RoomLevers.Levers[2] = CreateLever(r, r\x - 837.0 * RoomScale, r\y + 152.0 * RoomScale, r\z + 887.0 * RoomScale)
			
			; ~ Locked room at the room's center
			d.Doors = CreateDoor(r, r\x, r\y, r\z, 0.0, False, HEAVY_DOOR)
			d\Locked = 1
			
			; ~ SCP-096's spawnpoint
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 352.0 * RoomScale, r\y + 0.5, r\z)
			EntityParent(r\Objects[0], r\OBJ)
			
			; ~ Guard's spawnpoint
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 1328.0 * RoomScale, r\y + 0.5, r\z + 528.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			r\Objects[2] = LoadMesh_Strict("GFX\Map\Rooms\room2_servers_hcz_hb.b3d", r\OBJ)
			r\ScriptedObject[2] = True
			EntityPickMode(r\Objects[2], 2)
			EntityAlpha(r\Objects[2], 0.0)
			HideEntity(r\Objects[2])
			
			CreateCustomCenter(r, r\x, r\z - 756.0 * RoomScale)
			;[End Block]
		Case r_room2_shaft
			;[Block]
			; ~ Side of Elevator Shaft room door 1
			d.Doors = CreateDoor(r, r\x + 256.0 * RoomScale, r\y, r\z + 744.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_1)
			
			; ~ Side of Elevator Shaft room door 2
			d.Doors = CreateDoor(r, r\x + 1551.0 * RoomScale, r\y, r\z + 496.0 * RoomScale, 0.0)
			
			; ~ Misc. door
			d.Doors = CreateDoor(r, r\x + 1984.0 * RoomScale, r\y, r\z + 744.0 * RoomScale, 90.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			; ~ Player's position after leaving the pocket dimension
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 1551.0 * RoomScale, r\y, r\z + 233.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreateButton(BUTTON_DEFAULT, r\x + 1180.0 * RoomScale, r\y + 180.0 * RoomScale, r\z - 552.0 * RoomScale, 0.0, 270.0, 0.0, 0, True)
			EntityParent(r\Objects[1], r\OBJ)
			
			de.Decals = CreateDecal(DECAL_BLOOD_2, r\x + 1334.0 * RoomScale, r\y - 796.0 * RoomScale + 0.01, r\z - 220.0 * RoomScale, 90.0, Rnd(360.0), 0.0, 0.25)
			EntityParent(de\OBJ, r\OBJ)
			
			it.Items = CreateItem("Level 2 Key Card", "key2", r\x + 990.0 * RoomScale, r\y + 233.0 * RoomScale, r\z + 431.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("First Aid Kit", "firstaid", r\x + 1035.0 * RoomScale, r\y + 145.0 * RoomScale, r\z + 56.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateRandomBattery(r\x + 1930.0 * RoomScale, r\y + 97.0 * RoomScale, r\z + 256.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateRandomBattery( r\x + 1137.0 * RoomScale, r\y + 161.0 * RoomScale, r\z + 432.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("ReVision Eyedrops", "eyedrops", r\x + 1930.0 * RoomScale, r\y + 225.0 * RoomScale, r\z + 128.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_test_hcz
			;[Block]
			; ~ DNA door
			d.Doors = CreateDoor(r, r\x + 720.0 * RoomScale, r\y, r\z, 0.0, False, HEAVY_DOOR, KEY_HAND_WHITE)
			r\RoomDoors.Doors[0] = d
			
			; ~ Door to the center
			d.Doors = CreateDoor(r, r\x - 624.0 * RoomScale, r\y - 1280.0 * RoomScale, r\z, 90.0, True)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.031, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.031, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			For xTemp = 0 To 1
				For zTemp = -1 To 1
					r\Objects[xTemp * 3 + (zTemp + 1)] = CreatePivot()
					PositionEntity(r\Objects[xTemp * 3 + (zTemp + 1)], r\x + (-236.0 + 280.0 * xTemp) * RoomScale, r\y - 700.0 * RoomScale, r\z + 384.0 * zTemp * RoomScale)
					EntityParent(r\Objects[xTemp * 3 + (zTemp + 1)], r\OBJ)
				Next
			Next
			
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x + 754.0 * RoomScale, r\y - 1248.0 * RoomScale, r\z)
			EntityParent(r\Objects[6], r\OBJ)
			
			r\Objects[7] = LoadMesh_Strict("GFX\Map\Rooms\room2_test_hcz_hb.b3d", r\OBJ)
			r\ScriptedObject[7] = True
			EntityPickMode(r\Objects[7], 2)
			EntityAlpha(r\Objects[7], 0.0)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x + 744.0 * RoomScale, r\y - 856.0 * RoomScale, r\z + 236.0 * RoomScale, 0.0)
			sc\FollowPlayer = True
			
;			it.Items = CreateItem("Document SCP-682", "paper", r\x + 656.0 * RoomScale, r\y - 1200.0 * RoomScale, r\z - 16.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_cont2_008
			;[Block]
			; ~ The doors to the containment chamber of SCP-008
			d.Doors = CreateDoor(r, r\x - 96.0 * RoomScale, r\y - 5104.0 * RoomScale, r\z - 384.0 * RoomScale, 180.0, True, ONE_SIDED_DOOR, KEY_CARD_4)
			d\AutoClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.08, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x - 96.0 * RoomScale, r\y - 5104.0 * RoomScale, r\z + 256.0 * RoomScale, 0.0, False, ONE_SIDED_DOOR, KEY_CARD_4)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], r\x + 70.0 * RoomScale, EntityY(d\Buttons[0], True), r\z - 24.0 * RoomScale, True)
			RotateEntity(d\Buttons[0], 0.0, -90.0, 0.0, True)
			r\RoomDoors.Doors[1] = d
			
			r\RoomDoors[0]\LinkedDoor = r\RoomDoors[1]
			r\RoomDoors[1]\LinkedDoor = r\RoomDoors[0]
			
			; ~ Observation room door
			d.Doors = CreateDoor(r, r\x - 816.0 * RoomScale, r\y - 5104.0 * RoomScale, r\z - 384.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_4)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.08, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			d\Locked = 1
			r\RoomDoors.Doors[2] = d
			
			; ~ Elevators doors
			d.Doors = CreateDoor(r, r\x + 448.0 * RoomScale, r\y, r\z, -90.0, True, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[3] = d
			
			d.Doors = CreateDoor(r, r\x + 448.0 * RoomScale, r\y - 5104.0 * RoomScale, r\z, -90.0, False, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[4] = d
			
			; ~ Misc doors
			d.Doors = CreateDoor(r, r\x + 96.0 * RoomScale, r\y - 5104.0 * RoomScale, r\z - 576.0 * RoomScale, 90.0)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.162, True)
			
			d.Doors = CreateDoor(r, r\x - 456.0 * RoomScale, r\y - 5104.0 * RoomScale, r\z - 736.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_4)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			; ~ The container
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 62.0 * RoomScale, r\y - 4985.0 * RoomScale, r\z + 889.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			; ~ The lid of the container
			r\Objects[1] = LoadRMesh("GFX\Map\Rooms\008_2_opt.rmesh", Null, False)
			ScaleEntity(r\Objects[1], RoomScale, RoomScale, RoomScale)
			PositionEntity(r\Objects[1], r\x - 62.0 * RoomScale, r\y - 4954.0 * RoomScale, r\z + 945.0 * RoomScale)
			RotateEntity(r\Objects[1], 85.0, 0.0, 0.0, True)
			EntityParent(r\Objects[1], r\OBJ)
			
			r\Objects[2] = CreateSprite()
			r\ScriptedObject[2] = True
			Tex = LoadTexture_Strict("GFX\Map\Textures\glass.png", 1 + 2)
			EntityTexture(r\Objects[2], Tex)
			DeleteSingleTextureEntryFromCache(Tex)
			
			r\Objects[3] = CopyEntity(r\Objects[2])
			
			For i = 2 To 3
				SpriteViewMode(r\Objects[i], 2)
				ScaleSprite(r\Objects[i], 194.0 * RoomScale * 0.5, 194.0 * RoomScale * 0.5)
				PositionEntity(r\Objects[i], r\x - 640.0 * RoomScale, r\y - 4881.0 * RoomScale, r\z + 800.0 * RoomScale)
				TurnEntity(r\Objects[i], 0.0, 90.0 + (180 * (i = 3)), 0.0)
				EntityParent(r\Objects[i], r\OBJ)
				HideEntity(r\Objects[i])
			Next
			
			; ~ SCP-173's spawnpoint
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x - 820.0 * RoomScale, r\y - 4985.0 * RoomScale, r\z + 657.0 * RoomScale)
			EntityParent(r\Objects[4], r\OBJ)
			
			; ~ SCP-173's attack point
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x - 384.0 * RoomScale, r\y - 4985.0 * RoomScale, r\z + 752.0 * RoomScale)
			EntityParent(r\Objects[5], r\OBJ)
			
			; ~ Red light
			r\Objects[6] = CreateRedLight(r\x - 622.0 * RoomScale, r\y - 4735.0 * RoomScale, r\z + 672.5 * RoomScale)
			r\ScriptedObject[6] = True
			EntityParent(r\Objects[6], r\OBJ)
			HideEntity(r\Objects[6])
			
			; ~ Spawnpoint for the scientist used in the "SCP-008-1's scene"
			r\Objects[7] = CreatePivot()
			PositionEntity(r\Objects[7], r\x + 160.0 * RoomScale, r\y + 670.0 * RoomScale, r\z - 384.0 * RoomScale)
			EntityParent(r\Objects[7], r\OBJ)
			
			; ~ Spawnpoint for the player
			r\Objects[8] = CreatePivot()
			PositionEntity(r\Objects[8], r\x, r\y + 672.0 * RoomScale, r\z + 350.0 * RoomScale)
			EntityParent(r\Objects[8], r\OBJ)
			
			; ~ Elevators pivots
			r\Objects[9] = CreatePivot()
			PositionEntity(r\Objects[9], r\x + 752.0 * RoomScale, r\y + 240.0 * RoomScale, r\z)
			EntityParent(r\Objects[9], r\OBJ)
			
			r\Objects[10] = CreatePivot()
			PositionEntity(r\Objects[10], r\x + 752.0 * RoomScale, r\y - 4864.0 * RoomScale, r\z)
			EntityParent(r\Objects[10], r\OBJ)
			
			it.Items = CreateItem("Hazmat Suit", "hazmat", r\x - 537.0 * RoomScale, r\y - 4895.0 * RoomScale, r\z - 66.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Document SCP-008", "paper", r\x - 944.0 * RoomScale, r\y - 5008.0 * RoomScale, r\z + 672.0 * RoomScale)
;			RotateEntity(it\Collider, 0.0, 0.0, 0.0)
;			EntityParent(it\Collider, r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x + 384.0 * RoomScale, r\y - 4654.0 * RoomScale, r\z + 1168.0 * RoomScale, 20.0)
			sc\Angle = 135.0 : sc\Turn = 45.0
			;[End Block]
		Case r_cont2_049
			;[Block]
			; ~ Elevator doors
			d.Doors = CreateDoor(r, r\x + 320.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, -90.0, True, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x + 3024.0 * RoomScale, r\y - 3520.0 * RoomScale, r\z + 1824.0 * RoomScale, -90.0, False, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[1] = d
			
			d.Doors = CreateDoor(r, r\x - 672.0 * RoomScale, r\y, r\z - 416.0 * RoomScale, 0.0, True, MT_ELEVATOR_DOOR)
			PositionEntity(d\ElevatorPanel[1], EntityX(d\ElevatorPanel[1], True), EntityY(d\ElevatorPanel[1], True) + 0.05, EntityZ(d\ElevatorPanel[1], True), True)
			r\RoomDoors.Doors[2] = d
			
			d.Doors = CreateDoor(r, r\x - 2766.0 * RoomScale, r\y - 3520.0 * RoomScale, r\z - 1600.0 * RoomScale, 0.0, False, MT_ELEVATOR_DOOR)
			PositionEntity(d\ElevatorPanel[1], EntityX(d\ElevatorPanel[1], True), EntityY(d\ElevatorPanel[1], True) + 0.05, EntityZ(d\ElevatorPanel[1], True), True)
			r\RoomDoors.Doors[3] = d
			
			; ~ Storage room doors
			d.Doors = CreateDoor(r, r\x + 272.0 * RoomScale, r\y - 3552.0 * RoomScale, r\z + 104.0 * RoomScale, 90.0, True)
			d\Locked = 1
			r\RoomDoors.Doors[4] = d
			
			d.Doors = CreateDoor(r, r\x + 272.0 * RoomScale, r\y - 3520.0 * RoomScale, r\z - 1824.0 * RoomScale, 90.0)
			d\Locked = 1
			r\RoomDoors.Doors[5] = d
			
			d.Doors = CreateDoor(r, r\x - 272.0 * RoomScale, r\y - 3520.0 * RoomScale, r\z + 1824.0 * RoomScale, 90.0, True)
			d\Locked = 1
			r\RoomDoors.Doors[6] = d
			
			d.Doors = CreateDoor(r, r\x - 272.0 * RoomScale, r\y - 3552.0 * RoomScale, r\z + 98.0 * RoomScale, -90.0, False, BIG_DOOR, KEY_CARD_3)
			d\MTFClose = False : d\Locked = 1
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.91, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.2 , True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.85, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.2, True)
			RotateEntity(d\Buttons[0], 0.0, -90.0, 0.0, True)
			RotateEntity(d\Buttons[1], 0.0, 90.0, 0.0, True)
			r\RoomDoors.Doors[7] = d
			
			; ~ Misc doors
			d.Doors = CreateDoor(r, r\x, r\y, r\z, 0.0, False, HEAVY_DOOR, KEY_HAND_BLACK)
			
			d.Doors = CreateDoor(r, r\x - 896.0 * RoomScale, r\y, r\z - 640.0 * RoomScale, 90.0, False, HEAVY_DOOR)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			d.Doors = CreateDoor(r, r\x - 2990.0 * RoomScale, r\y - 3520.0 * RoomScale, r\z - 1824.0 * RoomScale, 90.0, False, HEAVY_DOOR)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			d.Doors = CreateDoor(r, r\x - 2766.0 * RoomScale, r\y - 3520.0 * RoomScale, r\z - 2048.0 * RoomScale, 0.0, False, HEAVY_DOOR)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			
			d.Doors = CreateDoor(r, r\x + 2720.0 * RoomScale, r\y - 3520.0 * RoomScale, r\z + 2048.0 * RoomScale, 180.0, False, DEFAULT_DOOR)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			; ~ Power feed lever
			r\RoomLevers.Levers[0] = CreateLever(r, r\x + 865.0 * RoomScale, r\y - 3374.0 * RoomScale, r\z - 854.0 * RoomScale, 270.0)
			; ~ Generator lever
			r\RoomLevers.Levers[1] = CreateLever(r, r\x - 815.0 * RoomScale, r\y - 3400.0 * RoomScale, r\z + 1097.0 * RoomScale, 180.0)
			
			; ~ Elevators pivots
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 624.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 640.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 3328.0 * RoomScale, r\y - 3280.0 * RoomScale, r\z + 1824.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 672.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 112.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x - 2766.0 * RoomScale, r\y - 3280.0 * RoomScale, r\z - 1296.0 * RoomScale)
			EntityParent(r\Objects[3], r\OBJ)
			
			; ~ Zombie # 1
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x + 528.0 * RoomScale, r\y - 3440.0 * RoomScale, r\z + 96.0 * RoomScale)
			EntityParent(r\Objects[4], r\OBJ)
			
			; ~ Zombie # 2
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x + 64.0 * RoomScale, r\y - 3440.0 * RoomScale, r\z - 1000.0 * RoomScale)
			EntityParent(r\Objects[5], r\OBJ)
			
			; ~ Fuel pump sound emitter
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x - 832.0 * RoomScale, r\y - 3484.0 * RoomScale, r\z + 1572.0 * RoomScale)
			EntityParent(r\Objects[6], r\OBJ)
			
			; ~ Spawnpoint for the map layout document
			r\Objects[7] = CreatePivot()
			PositionEntity(r\Objects[7], r\x + 2720.0 * RoomScale, r\y - 3516.0 * RoomScale, r\z + 1824.0 * RoomScale)
			EntityParent(r\Objects[7], r\OBJ)
			
			r\Objects[8] = CreatePivot()
			PositionEntity(r\Objects[8], r\x - 2720.0 * RoomScale, r\y - 3516.0 * RoomScale, r\z - 1824.0 * RoomScale)
			EntityParent(r\Objects[8], r\OBJ)
			
;			it.Items = CreateItem("Document SCP-049", "paper", r\x - 693.0 * RoomScale, r\y - 3332.0 * RoomScale, r\z + 702.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Level 4 Key Card", "key4", r\x - 564.0 * RoomScale, r\y - 3412.0 * RoomScale, r\z + 698.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("First Aid Kit", "firstaid", r\x + 385.0 * RoomScale, r\y - 3412.0 * RoomScale, r\z + 271.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x - 672.0 * RoomScale, r\z - 640.0 * RoomScale)
			;[End Block]
		Case r_cont2_409
			;[Block]
			; ~ Elevators doors
			d.Doors = CreateDoor(r, r\x + 256.0 * RoomScale, r\y, r\z + 655.0 * RoomScale, -90.0, True, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x - 2336.0 * RoomScale, r\y - 4256.0 * RoomScale, r\z - 648.0 * RoomScale, -90.0, False, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[1] = d
			
			; ~ SCP-409 Chamber door
			d.Doors = CreateDoor(r, r\x - 4352.0 * RoomScale, r\y - 4256.0 * RoomScale, r\z + 1368.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_4)
			
			; ~ Elevator pivots
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 560.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 656.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 2032.0 * RoomScale, r\y - 4011.0 * RoomScale, r\z - 648.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			; ~ Touching pivot
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 4917.0 * RoomScale, r\y - 4310.0 * RoomScale, r\z + 2095.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			; ~ Sparks pivot
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x - 4523.0 * RoomScale, r\y - 4060.0 * RoomScale, r\z - 2097.0 * RoomScale)
			EntityParent(r\Objects[3], r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 3635.0 * RoomScale, r\y - 3840.0 * RoomScale, r\z + 1729.0 * RoomScale, 20.0)
			sc\Angle = 100.0 : sc\Turn = 45.0
			;[End Block]
		Case r_room2c_maintenance
			;[Block]
			d.Doors = CreateDoor(r, r\x - 272.0 * RoomScale, r\y, r\z - 768.0 * RoomScale, 90.0, False, HEAVY_DOOR, KEY_CARD_3)
			d\Locked = 1 : d\MTFClose = False : d\DisableWaypoint = True
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			em.Emitters = CreateEmitter(r, r\x + 512.0 * RoomScale, r\y - 76.0 * RoomScale, r\z - 688.0 * RoomScale, 0)
			em\RandAngle = 55.0 : em\Speed = 0.0005 : em\AlphaChange = -0.015 : em\SizeChange = 0.007
			TurnEntity(em\OBJ, -90.0, 0.0, 0.0)
			
;			it.Items = CreateItem("Dr. L's Note #2", "paper", r\x - 160.0 * RoomScale, r\y + 32.0 * RoomScale, r\z - 353.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x + 340.0 * RoomScale, r\z - 340.0 * RoomScale)
			;[End Block]
		Case r_room3_hcz
			;[Block]
			em.Emitters = CreateEmitter(r, r\x + 512.0 * RoomScale, r\y - 76.0 * RoomScale, r\z - 688.0 * RoomScale, 0)
			em\RandAngle = 55.0 : em\Speed = 0.0005 : em\AlphaChange = -0.015 : em\SizeChange = 0.007
			TurnEntity(em\OBJ, -90.0, 0.0, 0.0)
			
			em.Emitters = CreateEmitter(r, r\x - 512.0 * RoomScale, r\y - 76.0 * RoomScale, r\z - 688.0 * RoomScale, 0)
			em\RandAngle = 55.0 : em\Speed = 0.0005 : em\AlphaChange = -0.015 : em\SizeChange = 0.007
			TurnEntity(em\OBJ, -90.0, 0.0, 0.0)
			
			CreateCustomCenter(r, r\x, r\z - 425.0 * RoomScale)
			;[End Block]
		Case r_cont3_513
			;[Block]
			d.Doors = CreateDoor(r, r\x - 704.0 * RoomScale, r\y, r\z + 304.0 * RoomScale, 0.0, True, DEFAULT_DOOR, KEY_CARD_3)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.031, True)
			
			; ~ Dust decals
			For i = 0 To 9
				Select(i)
					Case 0
						;[Block]
						xTemp = 0.0
						zTemp = 300.0
						Scale = Rnd(0.8, 1.0)
						;[End Block]
					Case 1
						;[Block]
						xTemp = -87.0
						zTemp = 466.0
						Scale = Rnd(0.1, 0.2)
						;[End Block]
					Case 2
						;[Block]
						xTemp = -177.0
						zTemp = 467.0
						Scale = Rnd(0.2, 0.3)
						;[End Block]
					Case 3
						;[Block]
						xTemp = -79.0
						zTemp = 215.0
						Scale = Rnd(0.1, 0.2)
						;[End Block]
					Case 4
						;[Block]
						xTemp = -13.0
						zTemp = 201.0
						Scale = Rnd(0.1, 0.15)
						;[End Block]
					Case 5
						;[Block]
						xTemp = 85.0
						zTemp = 97.0
						Scale = Rnd(0.2, 0.3)
						;[End Block]
					Case 6
						;[Block]
						xTemp = 205.0
						zTemp = 180.0
						Scale = Rnd(0.1, 0.2)
						;[End Block]
					Case 7
						;[Block]
						xTemp = 235.0
						zTemp = 114.0
						Scale = Rnd(0.1, 0.2)
						;[End Block]
					Case 8
						;[Block]
						xTemp = 182.0
						zTemp = 47.0
						Scale = Rnd(0.1, 0.2)
						;[End Block]
					Case 9
						;[Block]
						xTemp = 52.0
						zTemp = 200.0
						Scale = Rnd(0.2, 0.3)
						;[End Block]
				End Select
				If i < 3
					yTemp = 0.0
				Else
					yTemp = 3.0
				EndIf
				de.Decals = CreateDecal(DECAL_CORROSIVE_1, r\x + xTemp * RoomScale, r\y + yTemp * RoomScale + 0.005, r\z + zTemp * RoomScale, 90.0, Rnd(360.0), 0.0, Scale, Rnd(0.6, 0.8), 1)
				EntityParent(de\OBJ, r\OBJ)
			Next
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 450.0 * RoomScale, r\y + 420.0 * RoomScale, r\z + 250.0 * RoomScale, 20.0)
			sc\Angle = 135.0 : sc\Turn = 0.0
			
			it.Items = CreateItem("SCP-513", "scp513", r\x, r\y + 196.0 * RoomScale, r\z + 655.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Blood-stained Note", "paper", r\x + 736.0 * RoomScale, r\y + 1.0, r\z + 48.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
;			
;			it.Items = CreateItem("Document SCP-513", "paper", r\x - 480.0 * RoomScale, r\y + 104.0 * RoomScale, r\z - 176.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_cont3_966
			;[Block]
			; ~ Observation room doors
			d.Doors = CreateDoor(r, r\x - 400.0 * RoomScale, r\y, r\z, 90.0, False, DEFAULT_DOOR, KEY_CARD_3)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x, r\y, r\z - 480.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_3)
			r\RoomDoors.Doors[1] = d
			
			; ~ DNA door
			d.Doors = CreateDoor(r, r\x - 712.0 * RoomScale, r\y, r\z - 288.0 * RoomScale, 0.0, False, HEAVY_DOOR, KEY_HAND_BLACK)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x, r\y + 0.5, r\z + 512.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x, r\y + 0.5, r\z)
			EntityParent(r\Objects[1], r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 355.0 * RoomScale, r\y + 450.0 * RoomScale, r\z + 321.0 * RoomScale, 30.0)
			sc\Angle = -45.0 : sc\Turn = 0.0
			
			it.Items = CreateItem("Night Vision Goggles", "nvg", r\x - 284.0 * RoomScale, r\y + 0.5, r\z + 198.0 * RoomScale)
			it\State = Rnd(0.0, 1000.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Level 3 Key Card", "key3", r\x + 237.0 * RoomScale, r\y + 0.5, r\z - 368.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x + 490.0 * RoomScale, r\z - 490.0 * RoomScale)
			;[End Block]
		Case r_room4_hcz
			;[Block]
			em.Emitters = CreateEmitter(r, r\x + 512.0 * RoomScale, r\y - 76.0 * RoomScale, r\z - 688.0 * RoomScale, 0)
			em\RandAngle = 55.0 : em\Speed = 0.0005 : em\AlphaChange = -0.015 : em\SizeChange = 0.007
			TurnEntity(em\OBJ, -90.0, 0.0, 0.0)
			
			em.Emitters = CreateEmitter(r, r\x - 512.0 * RoomScale, r\y - 76.0 * RoomScale, r\z - 688.0 * RoomScale, 0)
			em\RandAngle = 55.0 : em\Speed = 0.0005 : em\AlphaChange = -0.015 : em\SizeChange = 0.007
			TurnEntity(em\OBJ, -90.0, 0.0, 0.0)
			
			em.Emitters = CreateEmitter(r, r\x + 512.0 * RoomScale, r\y - 76.0 * RoomScale, r\z + 688.0 * RoomScale, 0)
			em\RandAngle = 55.0 : em\Speed = 0.0005 : em\AlphaChange = -0.015 : em\SizeChange = 0.007
			TurnEntity(em\OBJ, -90.0, 0.0, 0.0)
			
			em.Emitters = CreateEmitter(r, r\x - 512.0 * RoomScale, r\y - 76.0 * RoomScale, r\z + 688.0 * RoomScale, 0)
			em\RandAngle = 55.0 : em\Speed = 0.0005 : em\AlphaChange = -0.015 : em\SizeChange = 0.007
			TurnEntity(em\OBJ, -90.0, 0.0, 0.0)
			
			CreateCustomCenter(r, r\x, r\z - 425.0 * RoomScale)
			;[End Block]
		Case r_gate_a_entrance
			;[Block]
			; ~ Elevator door
			d.Doors = CreateDoor(r, r\x + 720.0 * RoomScale, r\y, r\z + 512.0 * RoomScale, -90.0, True, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x, r\y, r\z - 360.0 * RoomScale, 0.0, False, BIG_DOOR, KEY_CARD_5)
			d\DisableWaypoint = True : d\MTFClose = False
			PositionEntity(d\Buttons[1], r\x + 422.0 * RoomScale, EntityY(d\Buttons[1], True), r\z - 576.0 * RoomScale, True)
			RotateEntity(d\Buttons[1], 0.0, r\Angle - 90.0, 0.0, True)
			PositionEntity(d\Buttons[0], r\x - 522.0 * RoomScale, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			RotateEntity(d\Buttons[0], 0.0, r\Angle - 225.0, 0.0, True)
			r\RoomDoors.Doors[1] = d
			
			; ~ Elevator pivot
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 1024.0 * RoomScale, r\y, r\z + 512.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			CreateCustomCenter(r, r\x, r\z - 720.0 * RoomScale)
			;[End Block]
		Case r_gate_a
			;[Block]
			; ~ Misc doors
			d.Doors = CreateDoor(r, r\x, r\y, r\z - 1024.0 * RoomScale, 0.0)
			d\AutoClose = False : d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 630.0 * RoomScale, r\y, r\z - 480.0 * RoomScale, 0.0)
			d\AutoClose = False : d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 1090.0 * RoomScale, r\y, r\z - 480.0 * RoomScale, 0.0)
			d\AutoClose = False : d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x, r\y, r\z + 2336.0 * RoomScale, 0.0, True, BIG_DOOR)
			d\Locked = 1 : d\MTFClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			
			d.Doors = CreateDoor(r, r\x + 5712.0 * RoomScale, r\y - 1248.0 * RoomScale, r\z + 4968.0 * RoomScale, 90.0)
			d\AutoClose = False : d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.08, True)
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			For r2.Rooms = Each Rooms
				If r2\RoomTemplate\RoomID = r_gate_a_entrance
					; ~ Elevator door
					d.Doors = CreateDoor(r, r\x + 1528.0 * RoomScale, r\y, r\z - 64.0 * RoomScale, -90.0, False, MT_ELEVATOR_DOOR)
					r\RoomDoors.Doors[1] = d
					
					r2\Objects[1] = CreatePivot()
					PositionEntity(r2\Objects[1], r\x + 1832.0 * RoomScale, r\y, r\z - 64.0 * RoomScale)
					EntityParent(r2\Objects[1], r\OBJ)
					Exit
				EndIf
			Next
			
			; ~ The door leading to ending tunnel
			d.Doors = CreateDoor(r, r\x - 4064.0 * RoomScale, r\y - 1248.0 * RoomScale, r\z + 3952.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_2)
			d\AutoClose = False
			
			; ~ Doors inside a tiny tunnel
			d.Doors = CreateDoor(r, r\x - 1400.0 * RoomScale, r\y - 480.0 * RoomScale, r\z + 2320.0 * RoomScale, 0.0, me\SelectedEnding = Ending_A2, DEFAULT_DOOR, KEY_CARD_2)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.12, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.12, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			d.Doors = CreateDoor(r, r\x - 1400.0 * RoomScale, r\y - 480.0 * RoomScale, r\z + 4352.0 * RoomScale, 0.0, me\SelectedEnding = Ending_A2, DEFAULT_DOOR, KEY_CARD_2)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.12, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.12, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			; ~ SCP-106's spawnpoint
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 1188.0 * RoomScale, r\y, r\z + 1960.0 * RoomScale)
			EntityParent(r\Objects[3], r\OBJ)
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x, r\y + 96.0 * RoomScale, r\z + 6400.0 * RoomScale)
			EntityParent(r\Objects[4], r\OBJ)
			
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x + 1784.0 * RoomScale, r\y + 2124.0 * RoomScale, r\z + 4512.0 * RoomScale)
			EntityParent(r\Objects[5], r\OBJ)
			
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x - 5048.0 * RoomScale, r\y + 1912.0 * RoomScale, r\z + 4656.0 * RoomScale)
			EntityParent(r\Objects[6], r\OBJ)
			
			; ~ MTF spawnpoint
			r\Objects[7] = CreatePivot()
			PositionEntity(r\Objects[7], r\x + 1824.0 * RoomScale, r\y + 0.22, r\z + 7056.0 * RoomScale)
			EntityParent(r\Objects[7], r\OBJ)
			
			r\Objects[8] = CreatePivot()
			PositionEntity(r\Objects[8], r\x - 1824.0 * RoomScale, r\y + 0.22, r\z + 7056.0 * RoomScale)
			EntityParent(r\Objects[8], r\OBJ)
			
			r\Objects[11] = CreatePivot()
			PositionEntity(r\Objects[11], r\x - 4064.0 * RoomScale, r\y - 1248.0 * RoomScale, r\z - 1696.0 * RoomScale)
			EntityParent(r\Objects[11], r\OBJ)
			
			r\Objects[13] = LoadMesh_Strict("GFX\Map\Rooms\gateawall1.b3d", r\OBJ)
			PositionEntity(r\Objects[13], r\x - 4308.0 * RoomScale, r\y - 1045.0 * RoomScale, r\z + 544.0 * RoomScale, True)
			EntityColor(r\Objects[13], 25.0, 25.0, 25.0)
			EntityType(r\Objects[13], HIT_MAP)
			
			r\Objects[14] = LoadMesh_Strict("GFX\Map\Rooms\gateawall2.b3d", r\OBJ)
			PositionEntity(r\Objects[14], r\x - 3820.0 * RoomScale, r\y - 1045.0 * RoomScale, r\z + 544.0 * RoomScale, True)
			EntityColor(r\Objects[14], 25.0, 25.0, 25.5)
			EntityType(r\Objects[14], HIT_MAP)
			
			r\Objects[15] = CreatePivot()
			PositionEntity(r\Objects[15], r\x - 3568.0 * RoomScale, r\y - 1089.0 * RoomScale, r\z + 4944.0 * RoomScale)
			EntityParent(r\Objects[15], r\OBJ)
			
			; ~ Hit Box
			r\Objects[16] = LoadMesh_Strict("GFX\Map\Rooms\gatea_hitbox1.b3d", r\OBJ)
			r\ScriptedObject[16] = True
			EntityPickMode(r\Objects[16], 2)
			EntityType(r\Objects[16], HIT_MAP)
			EntityAlpha(r\Objects[16], 0.0)
			;[End Block]
		Case r_gate_b_entrance
			;[Block]
			d.Doors = CreateDoor(r, r\x + 720.0 * RoomScale, r\y, r\z + 1440.0 * RoomScale, 0.0, True, MT_ELEVATOR_DOOR)
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x, r\y, r\z - 320.0 * RoomScale, 0.0, False, BIG_DOOR, KEY_CARD_5)
			d\DisableWaypoint = True : d\MTFClose = False
			PositionEntity(d\Buttons[1], r\x + 390.0 * RoomScale, EntityY(d\Buttons[1], True), r\z - 528.0 * RoomScale, True)
			RotateEntity(d\Buttons[1], 0.0, r\Angle - 90.0, 0.0, True)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), r\z - 198.0 * RoomScale, True)
			RotateEntity(d\Buttons[0], 0.0, r\Angle - 180.0, 0.0, True)
			r\RoomDoors.Doors[1] = d
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 720.0 * RoomScale, r\y, r\z + 1744.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			CreateCustomCenter(r, r\x, r\z - 720.0 * RoomScale)
			;[End Block]
		Case r_gate_b
			;[Block]
			For r2.Rooms = Each Rooms
				If r2\RoomTemplate\RoomID = r_gate_b_entrance
					; ~ Elevator
					d.Doors = CreateDoor(r, r\x - 5424.0 * RoomScale, r\y, r\z - 1372.0 * RoomScale, 0.0, False, MT_ELEVATOR_DOOR)
					r\RoomDoors.Doors[1] = d
					
					r2\Objects[1] = CreatePivot()
					PositionEntity(r2\Objects[1], r\x - 5424.0 * RoomScale, r\y, r\z - 1068.0 * RoomScale)
					EntityParent(r2\Objects[1], r\OBJ)
					Exit
				EndIf
			Next
			
			; ~ Other doors
			d.Doors = CreateDoor(r, r\x + 4352.0 * RoomScale, r\y, r\z - 492.0 * RoomScale, 0.0)
			d\AutoClose = False
			r\RoomDoors.Doors[2] = d
			
			d.Doors = CreateDoor(r, r\x + 4352.0 * RoomScale, r\y, r\z + 498.0 * RoomScale, 0.0)
			d\AutoClose = False
			r\RoomDoors.Doors[3] = d
			
			d.Doors = CreateDoor(r, r\x + 3248.0 * RoomScale, r\y - 928.0 * RoomScale, r\z + 6400.0 * RoomScale, 0.0, False, ONE_SIDED_DOOR, KEY_MISC, CODE_LOCKED)
			d\Locked = 1
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			r\RoomDoors.Doors[4] = d
			
			d.Doors = CreateDoor(r, r\x + 3072.0 * RoomScale, r\y - 928.0 * RoomScale, r\z + 5800.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_3)
			
			; ~ Guard spawnpoint
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x + 5203.0 * RoomScale, r\y + 1444.0 * RoomScale, r\z - 1739.0 * RoomScale)
			EntityParent(r\Objects[2], r\OBJ)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 4363.0 * RoomScale, r\y - 248.0 * RoomScale, r\z + 2766.0 * RoomScale)
			EntityParent(r\Objects[3], r\OBJ)
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x + 5192.0 * RoomScale, r\y + 1408.0 * RoomScale, r\z - 4352.0 * RoomScale)
			EntityParent(r\Objects[4], r\OBJ)
			
			; ~ Walkway
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x + 4352.0 * RoomScale, r\y, r\z + 1344.0 * RoomScale)
			EntityParent(r\Objects[5], r\OBJ)
			
			; ~ SCP-682's paw
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x + 2816.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 2816.0 * RoomScale)
			EntityParent(r\Objects[6], r\OBJ)
			
			; ~ MTF spawnpoint
			r\Objects[8] = CreatePivot()
			PositionEntity(r\Objects[8], r\x + 3600.0 * RoomScale, r\y - 888.0 * RoomScale, r\z + 6623.0 * RoomScale)
			EntityParent(r\Objects[8], r\OBJ)
			
			; ~ "SCP-682" pivot
			r\Objects[9] = CreatePivot()
			PositionEntity(r\Objects[9], r\x + 3808.0 * RoomScale, r\y + 1536.0 * RoomScale, r\z - 13568.0 * RoomScale)
			EntityParent(r\Objects[9], r\OBJ)
			
			; ~ Apache radius
			r\Objects[10] = CreatePivot()
			PositionEntity(r\Objects[10], r\x - 7680.0 * RoomScale, r\y + 208.0 * RoomScale, r\z - 27048.0 * RoomScale)
			EntityParent(r\Objects[10], r\OBJ)
			
			; ~ Extra apache spawnpoint
			r\Objects[11] = CreatePivot()
			PositionEntity(r\Objects[11], r\x - 5424.0 * RoomScale, r\y, r\z - 1068.0 * RoomScale)
			EntityParent(r\Objects[11], r\OBJ)
			
			CreateCustomCenter(r, r\x - 5424.0 * RoomScale, r\z - 1700.0 * RoomScale)
			;[End Block]
		Case r_room1_lifts
			;[Block]
			d.Doors = CreateDoor(r, r\x - 239.0 * RoomScale, r\y, r\z + 96.0 * RoomScale, 0.0, False, MT_ELEVATOR_DOOR)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			FreeEntity(d\ElevatorPanel[0]) : d\ElevatorPanel[0] = 0
			
			d.Doors = CreateDoor(r, r\x + 239.0 * RoomScale, r\y, r\z + 96.0 * RoomScale, 0.0, False, MT_ELEVATOR_DOOR)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 1.2, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			FreeEntity(d\ElevatorPanel[0]) : d\ElevatorPanel[0] = 0
			
			sc.SecurityCams = CreateSecurityCam(r, r\x + 384.0 * RoomScale, r\y + 384.0 * RoomScale, r\z - 960.0 * RoomScale, 20.0)
			sc\Angle = 45.0 : sc\Turn = 45.0
			;[End Block]
		Case r_room1_o5
			;[Block]
			d.Doors = CreateDoor(r, r\x, r\y, r\z - 240.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_MISC, CODE_O5_COUNCIL)
			
;			it.Items = CreateItem("Field Agent Log #235-001-CO5", "paper", r\x, r\y + 200.0 * RoomScale, r\z + 870.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
;			
;			it.Items = CreateItem("Groups of Interest Log", "paper", r\x + 100.0 * RoomScale, r\y + 200.0 * RoomScale, r\z + 100.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("First Aid Kit", "firstaid", r\x + 680.0 * RoomScale, r\y + 260.0 * RoomScale, r\z + 892.5 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateRandomBattery(r\x - 700.0 * RoomScale, r\y + 210.0 * RoomScale, r\z + 920.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Ballistic Helmet", "helmet", r\x + 344.0 * RoomScale, r\y + 210.0 * RoomScale, r\z - 900.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("SCP-268", "scp268", r\x + 290.0 * RoomScale, r\y + 200.0 * RoomScale, r\z + 170.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x, r\z - 639.0 * RoomScale)
			;[End Block]
		Case r_room2_ez
			;[Block]
;			it.Items = CreateItem("Document SCP-106", "paper", r\x + 404.0 * RoomScale, r\y + 145.0 * RoomScale, r\z + 559.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Level 2 Key Card", "key2", r\x - 156.0 * RoomScale, r\y + 151.0 * RoomScale, r\z + 72.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("S-NAV Navigator", "nav", r\x + 305.0 * RoomScale, r\y + 153.0 * RoomScale, r\z + 944.0 * RoomScale)
			it\State = Rnd(100.0)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Notification", "paper", r\x - 137.0 * RoomScale, r\y + 153.0 * RoomScale, r\z + 464.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_2_ez
			;[Block]
			it.Items = CreateItem("Level 1 Key Card", "key1", r\x - 368.0 * RoomScale, r\y - 48.0 * RoomScale, r\z + 80.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Document SCP-895", "paper", r\x - 800.0 * RoomScale, r\y - 48.0 * RoomScale, r\z + 368.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
;			
;			If Rand(2) = 1
;				it.Items = CreateItem("Document SCP-860", "paper", r\x - 800.0 * RoomScale, r\y - 48.0 * RoomScale, r\z - 464.0 * RoomScale)
;			Else
;				it.Items = CreateItem("SCP-093 Recovered Materials", "paper", r\x - 800.0 * RoomScale, r\y - 48.0 * RoomScale, r\z - 464.0 * RoomScale)
;			EndIf
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("S-NAV Navigator", "nav", r\x - 336.0 * RoomScale, r\y - 48.0 * RoomScale, r\z - 480.0 * RoomScale)
			it\State = Rnd(100.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_3_ez
			;[Block]
			; ~ Misc doors
			; ~ Upper floor office door
			d.Doors = CreateDoor(r, r\x - 1056.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 290.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_3)
			
			; ~ Upper floor Storage room door
			d.Doors = CreateDoor(r, r\x - 1056.0 * RoomScale, r\y + 384.0 * RoomScale, r\z - 736.0 * RoomScale, 270.0, True, ONE_SIDED_DOOR, KEY_CARD_2)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 1.2, True)
			
			For r2.Rooms = Each Rooms
				If r2 <> r
					If r2\RoomTemplate\RoomID = r_room2_3_ez
						r\Objects[0] = CopyEntity(r2\Objects[0], r\OBJ) ; ~ Don't load the mesh again
						Exit
					EndIf
				EndIf
			Next
			If r\Objects[0] = 0 Then r\Objects[0] = LoadMesh_Strict("GFX\Map\Rooms\room2_3_ez_hb.b3d", r\OBJ)
			r\ScriptedObject[0] = True
			EntityPickMode(r\Objects[0], 2)
			EntityType(r\Objects[0], HIT_MAP)
			EntityAlpha(r\Objects[0], 0.0)
			
;			If Rand(2) = 1
;				it.Items = CreateItem("Mobile Task Forces", "paper", r\x + 744.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 944.0 * RoomScale)
;				EntityParent(it\Collider, r\OBJ)
;			Else
;				it.Items = CreateItem("Security Clearance Levels", "paper", r\x + 680.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 944.0 * RoomScale)
;				EntityParent(it\Collider, r\OBJ)
;			EndIf
;			
;			it.Items = CreateItem("Object Classes", "paper", r\x + 160.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 568.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
;			
;			it.Items = CreateItem("Document", "paper", r\x - 1440.0 * RoomScale, r\y + 624.0 * RoomScale, r\z + 152.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Radio Transceiver", "radio", r\x - 1184.0 * RoomScale, r\y + 480.0 * RoomScale, r\z - 800.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("ReVision Eyedrops", "eyedrops", r\x - 1530.0 * RoomScale, r\y + 563.0 * RoomScale, r\z - 525.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			If Rand(3) = 1
				it.Items = CreateItem("ReVision Eyedrops", "eyedrops", r\x - 1530.0 * RoomScale, r\y + 563.0 * RoomScale, r\z - 625.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			it.Items = CreateRandomBattery(r\x - 1545.0 * RoomScale, r\y + 605.0 * RoomScale, r\z - 392.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			If Rand(2) = 1
				it.Items = CreateRandomBattery(r\x - 1540.0 * RoomScale, r\y + 495.0 * RoomScale, r\z - 320.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			If Rand(2) = 1
				it.Items = CreateRandomBattery(r\x - 1529.0 * RoomScale, r\y + 605.0 * RoomScale, r\z - 308.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			;[End Block]
		Case r_room2_bio
			;[Block]
			d.Doors = CreateDoor(r, r\x + 234.0 * RoomScale, r\y, r\z - 768.0 * RoomScale, 90.0, False, OFFICE_DOOR)
			d\Locked = 2 : d\MTFClose = False
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 475.0 * RoomScale, r\y + 385.0 * RoomScale, r\z + 305.0 * RoomScale, 20.0)
			sc\Angle = 225.0 : sc\Turn = 30.0
			;[End Block]
		Case r_room2_cafeteria
			;[Block]
			; ~ Misc doors
			d.Doors = CreateDoor(r, r\x + 1712.0 * RoomScale, r\y - 384.0 * RoomScale, r\z - 1024.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_3)
			d\Locked = 1 : d\MTFClose = False : d\DisableWaypoint = True
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 464.0 * RoomScale, r\y - 384.0 * RoomScale, r\z - 1024.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_3)
			d\Locked = 1 : d\MTFClose = False : d\DisableWaypoint = True
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			; ~ SCP-294
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 1847.0 * RoomScale, r\y - 240.0 * RoomScale, r\z - 321.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			; ~ Spawnpoint for the cups
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 1780.0 * RoomScale, r\y - 248.0 * RoomScale, r\z - 276.0 * RoomScale)
			EntityParent(r\Objects[1], r\OBJ)
			
			it.Items = CreateItem("Cup", "cup", r\x - 508.0 * RoomScale, r\y - 187.0 * RoomScale, r\z + 284.0 * RoomScale, 240, 175, 70)
			it\Name = JsonGetArrayValue(I_294\Drinks, S2IMapGet(I_294\DrinksMap, Upper("Orange Juice")))
			it\DisplayName = Format(GetLocalString("items", "cupof"), GetLocalString("misc", "orange"))
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Cup", "cup", r\x + 1412.0 * RoomScale, r\y - 187.0 * RoomScale, r\z - 716.0 * RoomScale, 87, 62, 45)
			it\Name = JsonGetArrayValue(I_294\Drinks, S2IMapGet(I_294\DrinksMap, Upper("Coffee")))
			it\DisplayName = Format(GetLocalString("items", "cupof"), GetLocalString("misc", "coffee"))
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Empty Cup", "emptycup", r\x - 540.0 * RoomScale, r\y - 187.0 * RoomScale, r\z + 124.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Quarter", "25ct", r\x - 447.0 * RoomScale, r\y - 334.0 * RoomScale, r\z + 36.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Quarter", "25ct", r\x + 1409.0 * RoomScale, r\y - 334.0 * RoomScale, r\z - 732.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_ic
			;[Block]
			d.Doors = CreateDoor(r, r\x - 896.0 * RoomScale, r\y, r\z, 90.0, True, MT_ELEVATOR_DOOR)
			d\Locked = 1 : d\MTFClose = False
			
			de.Decals = CreateDecal(DECAL_WATER, r\x - 711.0 * RoomScale, r\y + 0.005, r\z + 140.0 * RoomScale, 90.0, Rnd(360.0), 0.0, Rnd(0.8, 1.0), 1.0)
			EntityParent(de\OBJ, r\OBJ)
			
			it.Items = CreateItem("Level 0 Key Card", "key0", r\x - 1000.0 * RoomScale, r\y + 140.0 * RoomScale, r\z + 165.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Cup", "cup", r\x - 100.0 * RoomScale, r\y + 230.0 * RoomScale, r\z - 24.0 * RoomScale, 200, 200, 200)
			it\Name = JsonGetArrayValue(I_294\Drinks, S2IMapGet(I_294\DrinksMap, Upper("Water")))
			it\DisplayName = Format(GetLocalString("items", "cupof"), GetLocalString("misc", "water"))
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Empty Cup", "emptycup", r\x + 143.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 966.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x - 400.0 * RoomScale, r\z)
			;[End Block]
		Case r_room2_medibay
			;[Block]
			; ~ Medical bay door 1
			d.Doors = CreateDoor(r, r\x - 256.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_3)
			
			; ~ Medical bay door 2
			d.Doors = CreateDoor(r, r\x - 512.0 * RoomScale, r\y, r\z + 378.0 * RoomScale, 0.0, False, OFFICE_DOOR)
			
			; ~ Misc. door
			d.Doors = CreateDoor(r, r\x - 1104.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, 270.0, False, DEFAULT_DOOR, KEY_CARD_3)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			If Rand(2) = 1
				ItemName = "Syringe"
				ItemTempName = "syringe"
			Else
				ItemName = "Syringe"
				ItemTempName = "syringeinf"
			EndIf
			it.Items = CreateItem(ItemName, ItemTempName, r\x - 923.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 96.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			If Rand(2) = 1
				ItemName = "Syringe"
				ItemTempName = "syringe"
			Else
				ItemName = "Syringe"
				ItemTempName = "syringeinf"
			EndIf
			it.Items = CreateItem(ItemName, ItemTempName, r\x - 907.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 159.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Compact First Aid Kit", "finefirstaid", r\x - 333.0 * RoomScale, r\y + 192.0 * RoomScale, r\z - 123.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_office
			;[Block]
			d.Doors = CreateDoor(r, r\x - 244.0 * RoomScale, r\y, r\z, 90.0)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.048, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.048, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			; ~ Misc. doors
			d.Doors = CreateDoor(r, r\x - 1216.0 * RoomScale, r\y - 384.0 * RoomScale, r\z - 1024.0 * RoomScale, 0.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 1216.0 * RoomScale, r\y - 384.0 * RoomScale, r\z + 1024.0 * RoomScale, 180.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
;			it.Items = CreateItem("Sticky Note", "paper", r\x - 991.0 * RoomScale, r\y - 242.0 * RoomScale, r\z + 904.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_office_2
			;[Block]
			; ~ Misc. door
			d.Doors = CreateDoor(r, r\x + 234.0 * RoomScale, r\y, r\z, 90.0, False, OFFICE_DOOR)
			
			r\Objects[0] = LoadMesh_Strict("GFX\Map\Rooms\room2_office_2_hb.b3d", r\OBJ)
			r\ScriptedObject[0] = True
			EntityPickMode(r\Objects[0], 2)
			EntityType(r\Objects[0], HIT_MAP)
			EntityAlpha(r\Objects[0], 0.0)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 475.0 * RoomScale, r\y + 385.0 * RoomScale, r\z + 305.0 * RoomScale, 20.0)
			sc\Angle = 225.0 : sc\Turn = 30.0
			
			it.Items = CreateRandomBattery(r\x + 360.0 * RoomScale, r\y + 230.0 * RoomScale, r\z + 960.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			If Rand(2) = 1
				it.Items = CreateRandomBattery(r\x + 435.0 * RoomScale, r\y + 230.0 * RoomScale, r\z + 960.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			;[End Block]
		Case r_room2_office_3
			;[Block]
			; ~ Director Rosewood door
			d.Doors = CreateDoor(r, r\x + 1456.0 * RoomScale, r\y + 224.0 * RoomScale, r\z, 90.0, False, DEFAULT_DOOR, KEY_CARD_5)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.1, True)
			
			d.Doors = CreateDoor(r, r\x + 463.0 * RoomScale, r\y, r\z, 90.0, False, DEFAULT_DOOR, KEY_CARD_5)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) - 0.1, True)
			
			it.Items = CreateItem("Some SCP-420-J", "scp420j", r\x + 1776.0 * RoomScale, r\y + 400.0 * RoomScale, r\z + 427.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Some SCP-420-J", "scp420j", r\x + 1858.0 * RoomScale, r\y + 400.0 * RoomScale, r\z + 435.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Level 5 Key Card", "key5", r\x + 2272.0 * RoomScale, r\y + 392.0 * RoomScale, r\z + 387.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, r\Angle, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Nuclear Device Document", "paper", r\x + 2272.0 * RoomScale, r\y + 440.0 * RoomScale, r\z + 372.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Radio Transceiver", "radio", r\x + 2272.0 * RoomScale, r\y + 320.0 * RoomScale, r\z + 128.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_servers_ez
			;[Block]
			d.Doors = CreateDoor(r, r\x + 256.0 * RoomScale, r\y, r\z + 672.0 * RoomScale, 270.0, False, DEFAULT_DOOR, KEY_CARD_4)
			
			d.Doors = CreateDoor(r, r\x - 512.0 * RoomScale, r\y - 768.0 * RoomScale, r\z - 320.0 * RoomScale, 180.0, False, ONE_SIDED_DOOR, KEY_CARD_4)
			
			d.Doors = CreateDoor(r, r\x - 512.0 * RoomScale, r\y - 768.0 * RoomScale, r\z - 1040.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_4)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			it.Items = CreateItem("Night Vision Goggles", "nvg", r\x + 48.0 * RoomScale, r\y - 648.0 * RoomScale, r\z + 784.0 * RoomScale)
			it\State = Rnd(0.0, 1000.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_scientists
			;[Block]
			; ~ Dr. Maynard's office door
			d.Doors = CreateDoor(r, r\x + 256.0 * RoomScale, r\y, r\z + 448.0 * RoomScale, 270.0, False, DEFAULT_DOOR, KEY_MISC, Str(CODE_DR_MAYNARD))
			
			; ~ Dr.Gear's inaccessible office door
			d.Doors = CreateDoor(r, r\x - 448.0 * RoomScale, r\y, r\z, 270.0, False, DEFAULT_DOOR, KEY_MISC, CODE_LOCKED)
			d\Locked = 1 : d\MTFClose = False : d\DisableWaypoint = True
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			; ~ Dr. Harp's office door
			d.Doors = CreateDoor(r, r\x + 256.0 * RoomScale, r\y, r\z - 576.0 * RoomScale, 270.0, False, DEFAULT_DOOR, KEY_MISC, Str(CODE_DR_HARP))
			
;			it.Items = CreateItem("Mysterious Note", "paper", r\x + 736.0 * RoomScale, r\y + 224.0 * RoomScale, r\z + 544.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Ballistic Vest", "vest", r\x + 608.0 * RoomScale, r\y + 112.0 * RoomScale, r\z + 32.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
;			it.Items = CreateItem("Incident Report SCP-106-0204", "paper", r\x + 704.0 * RoomScale, r\y + 183.0 * RoomScale, r\z - 576.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
;			
;			it.Items = CreateItem("Journal Page", "paper", r\x + 912.0 * RoomScale, r\y + 176.0 * RoomScale, r\z - 160.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("First Aid Kit", "firstaid", r\x + 912.0 * RoomScale, r\y + 112.0 * RoomScale, r\z - 336.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			If I_005\ChanceToSpawn = 3
				it.Items = CreateItem("SCP-005", "scp005", r\x + 736.0 * RoomScale, r\y + 224.0 * RoomScale, r\z + 755.0 * RoomScale)
			Else
				it.Items = CreateItem("Level 4 Key Card", "key4", r\x + 736.0 * RoomScale, r\y + 224.0 * RoomScale, r\z + 755.0 * RoomScale)
			EndIf
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2_scientists_2
			;[Block]
			; ~ Dr. L's office door
			d.Doors = CreateDoor(r, r\x - 448.0 * RoomScale, r\y, r\z, 90.0, False, DEFAULT_DOOR, KEY_MISC, CODE_DR_L)
			d\MTFClose = False : d\DisableWaypoint = True
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			r\RoomDoors.Doors[0] = d
			
			; ~ Conference Room 9B door
			d.Doors = CreateDoor(r, r\x + 256.0 * RoomScale, r\y, r\z, 270.0, False, DEFAULT_DOOR, KEY_CARD_5)
			
			de.Decals = CreateDecal(DECAL_CORROSIVE_1, r\x - 808.0 * RoomScale, r\y + 0.005, r\z - 72.0 * RoomScale, 90.0, Rnd(360.0), 0.0)
			EntityParent(de\OBJ, r\OBJ)
			
			de.Decals = CreateDecal(DECAL_BLOOD_1, r\x - 808.0 * RoomScale, r\y + 0.01, r\z - 72.0 * RoomScale, 90.0, Rnd(360.0), 0.0, 0.3)
			EntityParent(de\OBJ, r\OBJ)
			
			de.Decals = CreateDecal(DECAL_CORROSIVE_1, r\x - 432.0 * RoomScale, r\y + 0.01, r\z, 90.0, Rnd(360.0), 0.0)
			EntityParent(de\OBJ, r\OBJ)
			
;			it.Items = CreateItem("Dr. L's Burnt Note #1", "paper", r\x - 688.0 * RoomScale, r\y + 1.0, r\z - 16.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
;			
;			it.Items = CreateItem("Dr. L's Burnt Note #2", "paper", r\x - 808.0 * RoomScale, r\y + 1.0, r\z - 72.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
;			
;			it.Items = CreateItem("The Modular Site Project", "paper", r\x + 622.0 * RoomScale, r\y + 125.0 * RoomScale, r\z - 73.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_cont2_860_1
			;[Block]
			; ~ Doors to observation room
			d.Doors = CreateDoor(r, r\x + 928.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_HAND_YELLOW)
			
			d.Doors = CreateDoor(r, r\x + 928.0 * RoomScale, r\y, r\z - 640.0 * RoomScale, 0.0, True, DEFAULT_DOOR, KEY_HAND_YELLOW)
			d\MTFClose = False
			
			; ~ Doors to SCP-860-1's door itself
			d.Doors = CreateDoor(r, r\x + 416.0 * RoomScale, r\y, r\z - 640.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_3)
			
			d.Doors = CreateDoor(r, r\x + 416.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_3)
			
			; ~ SCP-860-1's door
			d.Doors = CreateDoor(r, r\x + 184.0 * RoomScale, r\y, r\z, 0.0, False, WOODEN_DOOR, KEY_860)
			d\Locked = 1 : d\DisableWaypoint = True
			r\RoomDoors.Doors[0] = d
			
			; ~ The forest
			If (Not I_Zone\HasCustomForest)
				fr.Forest = New Forest
				r\fr = fr
				GenForestGrid(fr)
				PlaceForest(fr, r\x, r\y + 30.0, r\z, r)
			EndIf
			
;			it.Items = CreateItem("Document SCP-860-1", "paper", r\x + 1158.0 * RoomScale, r\y + 250.0 * RoomScale, r\z - 17.0 * RoomScale)
;			RotateEntity(it\Collider, 0.0, r\Angle, 0.0)
;			EntityParent(it\Collider, r\OBJ)
			
			CreateCustomCenter(r, r\x + 927.0 * RoomScale, r\z)
			;[End Block]
		Case r_room2c_ez
			;[Block]
			sc.SecurityCams = CreateSecurityCam(r, r\x - 288.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 288.0 * RoomScale, 20.0)
			sc\Angle = 225.0 : sc\Turn = 45.0
			;[End Block]
		Case r_room2c_2_ez
			;[Block]
			; ~ Corner office door
			d.Doors = CreateDoor(r, r\x + 605.0 * RoomScale, r\y, r\z - 234.0 * RoomScale, 0.0, False, OFFICE_DOOR)
			
			For r2.Rooms = Each Rooms
				If r2 <> r
					If r2\RoomTemplate\RoomID = r_room2c_2_ez
						r\Objects[0] = CopyEntity(r2\Objects[0], r\OBJ) ; ~ Don't load the mesh again
						Exit
					EndIf
				EndIf
			Next
			If r\Objects[0] = 0 Then r\Objects[0] = LoadMesh_Strict("GFX\Map\Rooms\room2C_2_ez_hb.b3d", r\OBJ)
			r\ScriptedObject[0] = True
			EntityPickMode(r\Objects[0], 2)
			EntityType(r\Objects[0], HIT_MAP)
			EntityAlpha(r\Objects[0], 0.0)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 288.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 288.0 * RoomScale, 20.0)
			sc\Angle = 225.0 : sc\Turn = 45.0
			
			If Rand(5) = 1
				it.Items = CreateItem("ReVision Eyedrops", "eyedrops", r\x + 402.0 * RoomScale, r\y + 200.0 * RoomScale, r\z - 922.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			ElseIf Rand(4) = 1
				it.Items = CreateItem("First Aid Kit", "firstaid", r\x + 402.0 * RoomScale, r\y + 200.0 * RoomScale, r\z - 922.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			If Rand(4) = 1
				it.Items = CreateItem("Cup", "cup", r\x + 880.0 * RoomScale, r\y + 200.0 * RoomScale, r\z - 300.0 * RoomScale, 200, 200, 200)
				it\Name = JsonGetArrayValue(I_294\Drinks, S2IMapGet(I_294\DrinksMap, Upper("Coffee")))
				it\DisplayName = Format(GetLocalString("items", "cupof"), GetLocalString("misc", "coffee"))
				EntityParent(it\Collider, r\OBJ)
				
				it.Items = CreateRandomBattery(r\x + 943.0 * RoomScale, r\y + 250.0 * RoomScale, r\z - 934.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			ElseIf Rand(3) = 1
				it.Items = CreateRandomBattery(r\x + 880.0 * RoomScale, r\y + 200.0 * RoomScale, r\z - 300.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			;[End Block]
		Case r_room2c_ec
			;[Block]
			; ~ Electrical Center entrance door
			d.Doors = CreateDoor(r, r\x, r\y, r\z + 384.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_4)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.1, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			; ~ Electrical Center controls door
			d.Doors = CreateDoor(r, r\x - 704.0 * RoomScale, r\y + 896.0 * RoomScale, r\z + 736.0 * RoomScale, 90.0, False, ONE_SIDED_DOOR, KEY_CARD_4)
			
			r\RoomLevers.Levers[0] = CreateLever(r, r\x - 239.0 * RoomScale, r\y + 1104.0 * RoomScale, r\z + 632.0 * RoomScale, -90.0, True)
			r\RoomLevers.Levers[1] = CreateLever(r, r\x - 239.0 * RoomScale, r\y + 1104.0 * RoomScale, r\z + 568.0 * RoomScale, -90.0, True)
			r\RoomLevers.Levers[2] = CreateLever(r, r\x - 239.0 * RoomScale, r\y + 1104.0 * RoomScale, r\z + 504.0 * RoomScale, -90.0, True)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 265.0 * RoomScale, r\y + 1280.0 * RoomScale, r\z + 105.0 * RoomScale, 20.0)
			sc\Angle = 45.0 : sc\Turn = 45.0
			
;			it.Items = CreateItem("Note from Daniel", "paper", r\x - 400.0 * RoomScale, r\y + 1040.0 * RoomScale, r\z + 115.0 * RoomScale)
;			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room2c_gw_ez
			;[Block]
			; ~ Misc doors
			d.Doors = CreateDoor(r, r\x + 815.0 * RoomScale, r\y, r\z - 352.0 * RoomScale, 0.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.07, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x + 352.0 * RoomScale, r\y, r\z - 815.0 * RoomScale, 90.0)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.07, True)
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			d.Doors = CreateDoor(r, r\x - 512.0 * RoomScale, r\y, r\z - 400.0 * RoomScale, 90.0, True, ONE_SIDED_DOOR)
			d\Locked = 1 : d\MTFClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			
			d.Doors = CreateDoor(r, r\x + 400.0 * RoomScale, r\y, r\z + 512.0 * RoomScale, 180.0, True, ONE_SIDED_DOOR)
			d\Locked = 1 : d\MTFClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			
			; ~ Security cameras inside
			sc.SecurityCams = CreateSecurityCam(r, r\x + 512.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 384.0 * RoomScale, 40.0, True, r\x + 668.0 * RoomScale, r\y + 1.1, r\z - 96.0 * RoomScale, 0.0, 90.0, 0.0)
			sc\Angle = 135.0 : sc\Turn = 45.0
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 384.0 * RoomScale, r\y + 384.0 * RoomScale, r\z - 512.0 * RoomScale, 40.0, True, r\x + 96.0 * RoomScale, r\y + 1.1, r\z - 668.0 * RoomScale)
			sc\Angle = 315.0 : sc\Turn = 45.0
			
			; ~ The blood decals inside
			For i = 0 To 5
				de.Decals = CreateDecal(Rand(DECAL_BLOOD_1, DECAL_BLOOD_2), r\x + Rnd(-392.0, 520.0) * RoomScale, r\y + 3.0 * RoomScale + Rnd(0, 0.001), r\z + Rnd(-392.0, 520.0) * RoomScale, 90.0, Rnd(360.0), 0.0, Rnd(0.3, 0.6))
				EntityParent(de\OBJ, r\OBJ)
				de.Decals = CreateDecal(Rand(DECAL_BLOOD_DROP_1, DECAL_BLOOD_DROP_2), r\x + Rnd(-392.0, 520.0) * RoomScale, r\y + 3.0 * RoomScale + Rnd(0, 0.001), r\z + Rnd(-392.0, 520.0) * RoomScale, 90.0, Rnd(360.0), 0.0, Rnd(0.1, 0.6))
				EntityParent(de\OBJ, r\OBJ)
				de.Decals = CreateDecal(Rand(DECAL_BLOOD_DROP_1, DECAL_BLOOD_DROP_2), r\x + Rnd(-0.5, 0.5), r\y + 3.0 * RoomScale + Rnd(0, 0.001), r\z + Rnd(-0.5, 0.5), 90.0, Rnd(360.0), 0.0, Rnd(0.1, 0.6))
				EntityParent(de\OBJ, r\OBJ)
			Next
			;[End Block]
		Case r_room3_ez
			;[Block]
			d.Doors = CreateDoor(r, r\x + 605.0 * RoomScale, r\y, r\z - 234.0 * RoomScale, 0.0, False, OFFICE_DOOR)
			
			d.Doors = CreateDoor(r, r\x - 605.0 * RoomScale, r\y, r\z - 234.0 * RoomScale, 0.0, False, OFFICE_DOOR)
			
			For r2.Rooms = Each Rooms
				If r2 <> r
					If r2\RoomTemplate\RoomID = r_room3_ez
						r\Objects[0] = CopyEntity(r2\Objects[0], r\OBJ) ; ~ Don't load the mesh again
						Exit
					EndIf
				EndIf
			Next
			If r\Objects[0] = 0 Then r\Objects[0] = LoadMesh_Strict("GFX\Map\Rooms\room3_ez_hb.b3d", r\OBJ)
			r\ScriptedObject[0] = True
			EntityPickMode(r\Objects[0], 2)
			EntityType(r\Objects[0], HIT_MAP)
			EntityAlpha(r\Objects[0], 0.0)
			
			sc.SecurityCams = CreateSecurityCam(r, r\x - 320.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 512.0 * RoomScale, 20.0)
			sc\Angle = 225.0 : sc\Turn = 45.0
			
			it.Items = CreateRandomBattery(r\x - 937.0 * RoomScale, r\y + 260.0 * RoomScale, r\z - 937.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			If Rand(5) = 1
				it.Items = CreateItem("Radio Transceiver", "radio", r\x + 712.0 * RoomScale, r\y + 200.0 * RoomScale, r\z - 797.0 * RoomScale)
				it\State = Rnd(0.0, 100.0)
				EntityParent(it\Collider, r\OBJ)
			ElseIf Rand(4) = 1
				it.Items = CreateItem("S-NAV Navigator", "nav", r\x + 712.0 * RoomScale, r\y + 200.0 * RoomScale, r\z - 797.0 * RoomScale)
				it\State = Rnd(0.0, 100.0)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			If Rand(4) = 1
				it.Items = CreateItem("Cup", "cup", r\x + 880.0 * RoomScale, r\y + 200.0 * RoomScale, r\z - 300.0 * RoomScale, 200, 200, 200)
				it\Name = JsonGetArrayValue(I_294\Drinks, S2IMapGet(I_294\DrinksMap, Upper("Coffee Drink")))
				it\DisplayName = GetLocalString("items", "cupcoffee")
				EntityParent(it\Collider, r\OBJ)
				
				it.Items = CreateRandomBattery(r\x + 943.0 * RoomScale, r\y + 250.0 * RoomScale, r\z - 934.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			ElseIf Rand(3) = 1
				it.Items = CreateRandomBattery(r\x + 880.0 * RoomScale, r\y + 200.0 * RoomScale, r\z - 300.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			If Rand(2) = 1
				it.Items = CreateItem("Quarter", "25ct", r\x - 234.0 * RoomScale, r\y + 30.0 * RoomScale, r\z + 505.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			;[End Block]
		Case r_room3_2_ez
			;[Block]
			it.Items = CreateRandomBattery(r\x - 132.0 * RoomScale, r\y - 368.0 * RoomScale, r\z - 658.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			If Rand(2) = 1
				it.Items = CreateRandomBattery(r\x - 76.0 * RoomScale, r\y - 368.0 * RoomScale, r\z - 658.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			If Rand(2) = 1
				it.Items = CreateRandomBattery(r\x - 196.0 * RoomScale, r\y - 368.0 * RoomScale, r\z - 658.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			it.Items = CreateItem("S-NAV Navigator", "nav", r\x + 58.0 * RoomScale, r\y - 504.0 * RoomScale, r\z - 658.0 * RoomScale)
			it\State = Rnd(100.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room3_3_ez
			;[Block]
;			it.Items = CreateItem("Document SCP-970", "paper", r\x + 960.0 * RoomScale, r\y - 448.0 * RoomScale, r\z + 251.0 * RoomScale)
;			RotateEntity(it\Collider, 0.0, r\Angle, 0.0)
;			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Gas Mask", "gasmask", r\x + 954.0 * RoomScale, r\y - 504.0 * RoomScale, r\z + 235.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case r_room3_4_ez
			;[Block]
			sc.SecurityCams = CreateSecurityCam(r, r\x - 320.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 512.0 * RoomScale, 20.0)
			sc\Angle = 225.0 : sc\Turn = 45.0
			;[End Block]
		Case r_room3_gw
			;[Block]
			; ~ Gateway control room door
			d.Doors = CreateDoor(r, r\x - 31.0 * RoomScale, r\y, r\z - 458.0 * RoomScale, 0.0, False, DEFAULT_DOOR, KEY_CARD_3)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.09, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.04, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 1.035, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.04, True)
			
			; ~ Misc. doors
			d.Doors = CreateDoor(r, r\x - 728.0 * RoomScale, r\y, r\z - 458.0 * RoomScale, 0.0, False, DEFAULT_DOOR)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.04, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.04, True)
			
			d.Doors = CreateDoor(r, r\x - 222.5 * RoomScale, r\y, r\z - 736.0 * RoomScale, -90.0, False, DEFAULT_DOOR)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) + 0.052, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.052, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			d.Doors = CreateDoor(r, r\x + 174.0 * RoomScale, r\y, r\z - 736.0 * RoomScale, 90.0, False, DEFAULT_DOOR, KEY_CARD_3)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			FreeEntity(d\OBJ2) : d\OBJ2 = 0
			
			; ~ Gateway doors
			d.Doors = CreateDoor(r, r\x - 459.0 * RoomScale, r\y, r\z + 339.0 * RoomScale, 90.0, True, DEFAULT_DOOR)
			d\Locked = 1 : d\MTFClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x + 385.0 * RoomScale, r\y, r\z + 339.0 * RoomScale, 270.0, True, DEFAULT_DOOR)
			d\Locked = 1 : d\MTFClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			r\RoomDoors.Doors[1] = d
			
			r\RoomLevers.Levers[0] = CreateLever(r, r\x + 204.0 * RoomScale, r\y + 192.0 * RoomScale, r\z + 164.0 * RoomScale, 0.0, True)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 48.0 * RoomScale, r\y + 128.0 * RoomScale, r\z + 320.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			CreateCustomCenter(r, r\x - 32.0 * RoomScale, r\z - 736.0 * RoomScale)
			;[End Block]
		Case r_room3_office
			;[Block]
			d.Doors = CreateDoor(r, r\x + 768.0 * RoomScale, r\y, r\z + 234.0 * RoomScale, 180.0, True, OFFICE_DOOR)
			
			r\Objects[0] = LoadMesh_Strict("GFX\Map\Rooms\room3_office_hb.b3d", r\OBJ)
			r\ScriptedObject[0] = True
			EntityPickMode(r\Objects[0], 2)
			EntityType(r\Objects[0], HIT_MAP)
			EntityAlpha(r\Objects[0], 0.0)
			
			de.Decals = CreateDecal(DECAL_WATER, r\x + 236.0 * RoomScale, r\y + 0.005, r\z - 68.0 * RoomScale, 90.0, Rnd(360.0), 0.0, Rnd(0.5, 0.7), 1.0)
			EntityParent(de\OBJ, r\OBJ)
			;[End Block]
		Case r_dimension_106
			;[Block]
			; ~ The doors inside labyrinth
			Tex = LoadTexture_Strict("GFX\map\Textures\rockmoss.jpg")
			For i = 0 To 9
				Select(i)
					Case 0
						;[Block]
						xTemp = 5187.0
						zTemp = 2523.0
						Angle = 180.0
						;[End Block]
					Case 1
						;[Block]
						xTemp = 5521.0
						zTemp = 1641.0
						Angle = 180.0
						;[End Block]
					Case 2
						;[Block]
						xTemp = 9128.0
						zTemp = 2160.0
						Angle = 180.0
						;[End Block]
					Case 3
						;[Block]
						xTemp = 8523.0
						zTemp = 1728.0
						Angle = 180.0
						;[End Block]
					Case 4
						;[Block]
						xTemp = 9880.0
						zTemp = 1212.0
						Angle = 180.0
						;[End Block]
					Case 5
						;[Block]
						xTemp = 5299.0
						zTemp = 360.0
						Angle = 90.0
						;[End Block]
					Case 6
						;[Block]
						xTemp = 7807.0
						zTemp = 1259.0
						Angle = 90.0
						;[End Block]
					Case 7
						;[Block]
						xTemp = 8196.0
						zTemp = 1404.0
						Angle = 90.0
						;[End Block]
					Case 8
						;[Block]
						xTemp = 8143.0
						zTemp = 360.0
						Angle = 90.0
						;[End Block]
					Case 9
						;[Block]
						xTemp = 9709.0
						zTemp = 888.0
						Angle = 90.0
						;[End Block]
				End Select
				de.Decals = CreateDecal(DECAL_CORROSIVE_1, r\x + xTemp * RoomScale, r\y + 2574.0 * RoomScale + 0.05, r\z + 32.0 + zTemp * RoomScale, 90.0, 0.0, 0.0, Rnd(0.8, 1.0))
				d.Doors = CreateDoor(r, r\x + xTemp * RoomScale, r\y + 2574.0 * RoomScale, r\z + 32.0 + zTemp * RoomScale, Angle, False, DEFAULT_DOOR, KEY_005)
				EntityTexture(d\OBJ, Tex)
				If d\OBJ2 <> 0 Then EntityTexture(d\OBJ2, Tex)
				EntityTexture(d\FrameOBJ, Tex)
			Next
			DeleteSingleTextureEntryFromCache(Tex)
			
			; ~ The doors inside fake tunnel
			d.Doors = CreateDoor(r, r\x, r\y + 2060.0 * RoomScale, r\z + 32.0 - 1024.0 * RoomScale, 0.0, False, HEAVY_DOOR)
			d\AutoClose = False
			r\RoomDoors.Doors[0] = d
			
			d.Doors = CreateDoor(r, r\x, r\y + 2048.0 * RoomScale, r\z + 32.0 + 1024.0 * RoomScale, 180.0, False, HEAVY_DOOR)
			d\AutoClose = False
			r\RoomDoors.Doors[1] = d
			
			de.Decals = CreateDecal(DECAL_PD_6, r\x - (1536.0 * RoomScale), r\y + 0.02, r\z + 608.0 * RoomScale + 32.0, 90.0, 0.0, 0.0, 0.8, 1.0, 1, 2)
			
			Local Hallway% = LoadRMesh("GFX\Map\Rooms\dimension_106\dimension_106_2.rmesh", Null) ; ~ The tunnels in the first room
			
			For i = 1 To 8
				r\Objects[i - 1] = CopyEntity(Hallway)
				
				Angle = (i - 1) * (360.0 / 8.0)
				SinValue = Sin(Angle) * (512.0 * RoomScale)
				CosValue = Cos(Angle) * (512.0 * RoomScale)
				
				ScaleEntity(r\Objects[i - 1], RoomScale, RoomScale, RoomScale)
				EntityType(r\Objects[i - 1], HIT_MAP)
				EntityPickMode(r\Objects[i - 1], 2)
				RotateEntity(r\Objects[i - 1], 0.0, Angle - 90.0, 0.0)
				PositionEntity(r\Objects[i - 1], r\x + CosValue, r\y, r\z + SinValue)
				EntityParent(r\Objects[i - 1], r\OBJ)
				
				If i < 6
					de.Decals = CreateDecal(i + 7, r\x + CosValue * 2.0, r\y + 0.02, r\z + SinValue * 2.0, 90.0, Angle - 90.0, 0.0, 0.5, 1.0, 1, 2)
				EndIf
			Next
			FreeEntity(Hallway) : Hallway = 0
			
			r\Objects[8] = LoadRMesh("GFX\Map\Rooms\dimension_106\dimension_106_3.rmesh", Null) ; ~ The room with the throne, moving pillars etc 
			
			r\Objects[9] = LoadMesh_Strict("GFX\Map\Props\dimension_106_pillar.b3d") ; ~ The flying pillar
			
			r\Objects[10] = CopyEntity(r\Objects[9])
			
			r\Objects[11] = LoadRMesh("GFX\Map\Rooms\dimension_106\dimension_106_4.rmesh", Null) ; ~ The pillar room
			
			For i = 8 To 11
				ScaleEntity(r\Objects[i], RoomScale * ((i <> 10) + ((i = 10) * 1.5)), RoomScale * ((i <> 10) + ((i = 10) * 2.0)), RoomScale * ((i <> 10) + ((i = 10) * 1.5)))
				EntityType(r\Objects[i], HIT_MAP)
				EntityPickMode(r\Objects[i], 2)
				PositionEntity(r\Objects[i], r\x, r\y, r\z + 32.0 + (32.0 * (i = 11)), True)
			Next
			
			For i = 12 To 16
				r\Objects[i] = CreatePivot(r\Objects[11])
				Select(i)
					Case 12
						;[Block]
						PositionEntity(r\Objects[i], r\x, r\y + 200.0 * RoomScale, r\z + 64.0, True)
						;[End Block]
					Case 13
						;[Block]
						PositionEntity(r\Objects[i], r\x + 390.0 * RoomScale, r\y + 200.0 * RoomScale, r\z + 64.0 + 272.0 * RoomScale, True)
						;[End Block]
					Case 14
						;[Block]
						PositionEntity(r\Objects[i], r\x + 838.0 * RoomScale, r\y + 200.0 * RoomScale, r\z + 64.0 - 551.0 * RoomScale, True)
						;[End Block]
					Case 15
						;[Block]
						PositionEntity(r\Objects[i], r\x - 139.0 * RoomScale, r\y + 200.0 * RoomScale, r\z + 64.0 + 1201.0 * RoomScale, True)
						;[End Block]
					Case 16
						;[Block]
						PositionEntity(r\Objects[i], r\x - 1238.0 * RoomScale, r\y - 1664.0 * RoomScale, r\z + 64.0 + 381.0 * RoomScale, True)
						;[End Block]
				End Select
			Next
			
			r\Textures[0] = LoadTexture_Strict("GFX\NPCs\pd_plane.png", 1 + 2, DeleteAllTextures)
			
			r\Textures[1] = LoadTexture_Strict("GFX\NPCs\pd_plane_eye.png", 1 + 2, DeleteAllTextures)
			
			r\Objects[17] = CreateSprite()
			r\ScriptedObject[17] = True
			Tex = LoadTexture_Strict("GFX\NPCs\scp_106_eyes.png", 1, DeleteAllTextures)
			EntityTexture(r\Objects[17], Tex)
			DeleteSingleTextureEntryFromCache(Tex)
			PositionEntity(r\Objects[17], EntityX(r\Objects[8], True), r\y + 1376.0 * RoomScale, EntityZ(r\Objects[8], True) - 2848.0 * RoomScale)
			RotateEntity(r\Objects[17], 0.0, 180.0, 0.0)
			ScaleSprite(r\Objects[17], 0.03, 0.03)
			EntityBlend(r\Objects[17], 3)
			EntityFX(r\Objects[17], 1 + 8)
			SpriteViewMode(r\Objects[17], 2)
			
			r\Objects[18] = LoadMesh_Strict("GFX\Map\Props\throne_wall.b3d")
			r\ScriptedObject[18] = True
			PositionEntity(r\Objects[18], EntityX(r\Objects[8], True), r\y, EntityZ(r\Objects[8], True) - 864.5 * RoomScale)
			ScaleEntity(r\Objects[18], RoomScale / 2.04, RoomScale, RoomScale)
			EntityPickMode(r\Objects[18], 2)
			EntityType(r\Objects[18], HIT_MAP)
			EntityParent(r\Objects[18], r\OBJ)
			
			r\Objects[19] = CreateSprite()
			r\ScriptedObject[19] = True
			ScaleSprite(r\Objects[19], 8.0, 8.0)
			EntityTexture(r\Objects[19], r\Textures[0])
			EntityOrder(r\Objects[19], 100)
			EntityBlend(r\Objects[19], 2)
			EntityFX(r\Objects[19], 1 + 8)
			SpriteViewMode(r\Objects[19], 2)
			PositionEntity(r\Objects[19], EntityX(r\Objects[8], True) - 1000.0, 16.0, 0.0, True)
			
			r\Objects[20] = LoadMesh_Strict("GFX\Map\Rooms\dimension_106\dimension_106_terrain.b3d")
			r\ScriptedObject[20] = True
			Tex = LoadTexture_Strict("GFX\map\Textures\rockmoss.jpg")
			EntityTexture(r\Objects[20], Tex)
			DeleteSingleTextureEntryFromCache(Tex)
			ScaleEntity(r\Objects[20], RoomScale, RoomScale, RoomScale)
			EntityType(r\Objects[20], HIT_MAP)
			PositionEntity(r\Objects[20], r\x, r\y + 16.0 + 2944.0 * RoomScale, r\z + 32.0, True)
			
			For i = 17 To 19 Step 2
				HideEntity(r\Objects[i])
				HideEntity(r\Objects[i + 1])
			Next
			
			; ~ Exits from labyrinth
			r\Objects[21] = CreatePivot()
			PositionEntity(r\Objects[21], r\x + 10939.0 * RoomScale, r\y + 2650.0 * RoomScale, r\z + 32.0 + 1822.0 * RoomScale)
			EntityParent(r\Objects[21], r\OBJ)
			
			r\Objects[22] = CreatePivot()
			PositionEntity(r\Objects[22], r\x + 3396.0 * RoomScale, r\y + 2650.0 * RoomScale, r\z + 32.0 + 1641.0 * RoomScale)
			EntityParent(r\Objects[22], r\OBJ)
			
			; ~ SCP-106 spawnpoint in labyrinth
			r\Objects[23] = CreatePivot()
			PositionEntity(r\Objects[23], r\x + 7211.0 * RoomScale, r\y + 2650.0 * RoomScale, r\z + 32.0 + 1566.0 * RoomScale)
			EntityParent(r\Objects[23], r\OBJ)
			
			; ~ Player spawnpoints in labyrinth
			r\Objects[24] = CreatePivot()
			PositionEntity(r\Objects[24], r\x + 5261.0 * RoomScale, r\y + 2650.0 * RoomScale, r\z + 32.0 + 2700.0 * RoomScale)
			EntityParent(r\Objects[24], r\OBJ)
			
			r\Objects[25] = CreatePivot()
			PositionEntity(r\Objects[25], r\x + 6835.0 * RoomScale, r\y + 2650.0 * RoomScale, r\z + 32.0 + 2378.0 * RoomScale)
			EntityParent(r\Objects[25], r\OBJ)
			
			r\Objects[26] = CreatePivot()
			PositionEntity(r\Objects[26], r\x + 7925.0 * RoomScale, r\y + 2650.0 * RoomScale, r\z + 32.0 + 201.0 * RoomScale)
			EntityParent(r\Objects[26], r\OBJ)
			
			r\Objects[27] = CreatePivot()
			PositionEntity(r\Objects[27], r\x + 8380.0 * RoomScale, r\y + 2650.0 * RoomScale, r\z + 32.0 + 2301.0 * RoomScale)
			EntityParent(r\Objects[27], r\OBJ)
			
;			it.Items = CreateItem("Burnt Note", "paper", r\x, r\y + 0.5, r\z + 896.0 * RoomScale)
;			it.Items = CreateItem("George Maynard's Badge", "burntbadge", r\x - 1300 * RoomScale, r\y + 0.5, r\z + 8700 * RoomScale)
			;[End Block]
	End Select
	
	Local ts.TempScreens, twp.TempWayPoints, tl.TempLights, tp.TempProps, tse.TempSoundEmitters
	
	For ts.TempScreens = Each TempScreens
		If ts\RoomTemplate = r\RoomTemplate Then CreateScreen(r, r\x + ts\x, r\y + ts\y, r\z + ts\z, ts\ImgPath)
	Next
	
	For twp.TempWayPoints = Each TempWayPoints
		If twp\RoomTemplate = r\RoomTemplate Then CreateWaypoint(Null, r, r\x + twp\x, r\y + twp\y, r\z + twp\z)
	Next
	
	For tl.TempLights = Each TempLights
		If tl\RoomTemplate = r\RoomTemplate
			l.Lights = AddLight(r, r\x + tl\x, r\y + tl\y, r\z + tl\z, tl\lType, tl\Range, tl\R, tl\G, tl\B)
			
			If tl\lType = 3 Then RotateEntity(l\OBJ, tl\Pitch, tl\Yaw, 0.0)
		EndIf
	Next
	
	For tp.TempProps = Each TempProps
		If tp\RoomTemplate = r\RoomTemplate Then CreateProp(r, tp\Name, r\x + tp\x, r\y + tp\y, r\z + tp\z, tp\Pitch, tp\Yaw, tp\Roll, tp\ScaleX, tp\ScaleY, tp\ScaleZ, tp\HasCollision, tp\FX, tp\Texture)
	Next
	
	;If r\RoomTemplate\TempTriggerBoxAmount > 0
	;	r\TriggerBoxAmount = r\RoomTemplate\TempTriggerBoxAmount
	;	For i = 0 To r\TriggerBoxAmount - 1
	;		r\TriggerBoxes[i] = New TriggerBox
	;		r\TriggerBoxes[i]\OBJ = CopyEntity(r\RoomTemplate\TempTriggerBox[i], r\OBJ)
	;		EntityColor(r\TriggerBoxes[i]\OBJ, 255, 255, 0)
	;		EntityAlpha(r\TriggerBoxes[i]\OBJ, 0.0)
	;		r\TriggerBoxes[i]\Name = r\RoomTemplate\TempTriggerBoxName[i]
	;	Next
	;EndIf
	
	For tse.TempSoundEmitters = Each TempSoundEmitters
		If tse\RoomTemplate = r\RoomTemplate Then CreateSoundEmitter(r, tse\ID, r\x + tse\x, r\y + tse\y, r\z + tse\z, tse\Range)
	Next
	
	CatchErrors("Uncaught: FillRoom(Room ID: " + r\RoomTemplate\RoomID + ")")
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS