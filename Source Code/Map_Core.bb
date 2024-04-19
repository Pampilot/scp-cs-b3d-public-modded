RenderLoading(50, GetLocalString("loading", "core.mat"))

Include "Source Code\Materials_Core.bb"

RenderLoading(55, GetLocalString("loading", "core.texcache"))

Include "Source Code\Texture_Cache_Core.bb"

;[Block]
Type Props
	Field Name$
	Field OBJ%
	Field room.Rooms
End Type
;[End Block]

;[Block]
Type TempProps
	Field Name$
	Field x#, y#, z#
	Field Pitch#, Yaw#, Roll#
	Field ScaleX#, ScaleY#, ScaleZ#
	Field HasCollision%
	Field FX%
	Field Texture$
	Field RoomTemplate.RoomTemplates
End Type
;[End Block]

Function CheckForPropModel%(File$)
	Select(StripPath(File))
		Case "door01.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorModelID[DOOR_DEFAULT_MODEL]))
			;[End Block]
		Case "contdoorleft.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorModelID[DOOR_BIG_MODEL_1]))
			;[End Block]
		Case "contdoorright.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorModelID[DOOR_BIG_MODEL_2]))
			;[End Block]
		Case "officedoor.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorModelID[DOOR_OFFICE_MODEL]))
			;[End Block]
		Case "doorframe.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorFrameModelID[DOOR_DEFAULT_FRAME_MODEL]))
			;[End Block]
		Case "contdoorframe.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorFrameModelID[DOOR_BIG_FRAME_MODEL]))
			;[End Block]
		Case "officedoorframe.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorFrameModelID[DOOR_OFFICE_FRAME_MODEL]))
			;[End Block]
		Case "button.b3d"
			;[Block]
			Return(CopyEntity(d_I\ButtonModelID[BUTTON_DEFAULT]))
			;[End Block]
		Case "buttonkeycard.b3d"
			;[Block]
			Return(CopyEntity(d_I\ButtonModelID[BUTTON_KEYCARD]))
			;[End Block]
		Case "elevator_panel.b3d"
			;[Block]
			Return(CopyEntity(d_I\ElevatorPanelModel))
			;[End Block]
		Case "leverbase.b3d"
			;[Block]
			Return(CopyEntity(lvr_I\LeverModelID[LEVER_BASE_MODEL]))
			;[End Block]
		Case "leverhandle.b3d"
			;[Block]
			Return(CopyEntity(lvr_I\LeverModelID[LEVER_HANDLE_MODEL]))
			;[End Block]
		Case "monitor2.b3d"
			;[Block]
			Return(CopyEntity(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL]))
			;[End Block]
		Default
			;[Block]
			Return(LoadMesh_Strict(File))
			;[End Block]
	End Select
End Function

Function CreateProp.Props(room.Rooms, Name$, x#, y#, z#, Pitch#, Yaw#, Roll#, ScaleX#, ScaleY#, ScaleZ#, HasCollision%, FX%, Texture$)
	Local p.Props, p2.Props
	Local Tex%
	
	p.Props = New Props
	For p2.Props = Each Props
		If p2 <> p
			If p2\Name = Name
				p\OBJ = CopyEntity(p2\OBJ)
				Exit
			EndIf
		EndIf
	Next
	
	p\Name = Name
	p\room = room
	
	If p\OBJ = 0 Then p\OBJ = CheckForPropModel(Name) ; ~ A hacky optimization (just copy models that loaded as variable). Also fixes models folder if the CBRE was used
	PositionEntity(p\OBJ, x, y, z)
	RotateEntity(p\OBJ, Pitch, Yaw, Roll)
	If room <> Null Then EntityParent(p\OBJ, room\OBJ)
	ScaleEntity(p\OBJ, ScaleX, ScaleY, ScaleZ)
	EntityType(p\OBJ, HasCollision) ; ~ DON'T FORGET THAT Const HIT_MAP% = 1
	EntityFX(p\OBJ, FX)
	If Texture <> ""
		Tex = LoadTexture_Strict(Texture)
		If opt\Atmosphere Then TextureBlend(Tex, 5)
		EntityTexture(p\OBJ, Tex)
		DeleteSingleTextureEntryFromCache(Tex)
	EndIf
	EntityPickMode(p\OBJ, 2)
	
	Return(p)
End Function

Function RemoveProp%(pr.Props)
	FreeEntity(pr\OBJ) : pr\OBJ = 0
	Delete(pr) : pr = Null
End Function

;[Block]
Type TempLights
	Field RoomTemplate.RoomTemplates
	Field lType%
	Field x#, y#, z#
	Field Range#
	Field R%, G%, B%
	Field Pitch#, Yaw#
	Field InnerConeAngle%, OuterConeAngle#
End Type
;[End Block]

Global LightVolume#, TempLightVolume#

;[Block]
Type Lights
	Field OBJ%
	Field Sprite%, AdvancedSprite%
	Field x#, y#, z#
	Field Range#
	Field R%, G%, B%
	Field Intensity#
	Field Flickers% = False
	Field room.Rooms
End Type
;[End Block]

Function AddLight.Lights(room.Rooms, x#, y#, z#, Type_%, Range#, R%, G%, B%)
	Local i%, l.Lights, l2.Lights
	
	l.Lights = New Lights
	l\room = room
	
	l\OBJ = CreateLight(Type_)
	LightRange(l\OBJ, Range)
	LightColor(l\OBJ, R, G, B)
	PositionEntity(l\OBJ, x, y, z, True)
	If room <> Null Then EntityParent(l\OBJ, room\OBJ)
	HideEntity(l\OBJ)
	
	l\Sprite = CreateSprite()
	PositionEntity(l\Sprite, x, y, z)
	ScaleSprite(l\Sprite, 0.13 , 0.13)
	EntityTexture(l\Sprite, misc_I\LightSpriteID[LIGHT_SPRITE_DEFAULT])
	EntityFX(l\Sprite, 1 + 8)
	EntityBlend(l\Sprite, 3)
	EntityColor(l\Sprite, R, G, B)
	EntityParent(l\Sprite, l\OBJ)
	HideEntity(l\Sprite)
	
	l\AdvancedSprite = CreateSprite()
	PositionEntity(l\AdvancedSprite, x, y, z)
	ScaleSprite(l\AdvancedSprite, Rnd(0.36, 0.4), Rnd(0.36, 0.4))
	EntityTexture(l\AdvancedSprite, misc_I\AdvancedLightSprite)
	EntityFX(l\AdvancedSprite, 1 + 8)
	EntityBlend(l\AdvancedSprite, 3)
	EntityOrder(l\AdvancedSprite, -1)
	EntityColor(l\AdvancedSprite, R, G, B)
	RotateEntity(l\AdvancedSprite, 0.0, 0.0, Rnd(360.0))
	SpriteViewMode(l\AdvancedSprite, 1)
	EntityParent(l\AdvancedSprite, l\OBJ)
	HideEntity(l\AdvancedSprite)
	
	l\Intensity = (R + G + B) / 255.0 / 3.0
	If room <> Null
		If Rand(50) = 1
			Local RID% = room\RoomTemplate\RoomID
			
			If RID <> r_cont1_173_intro And RID <> r_gate_a And RID <> r_gate_b And RID <> r_dimension_106 And RID <> r_dimension_1499 Then l\Flickers = True
		EndIf
	EndIf
	Return(l)
End Function

Global IsBlackOut%, PrevIsBlackOut%
Global SecondaryLightOn#

Global UpdateLightsTimer#

Function UpdateLightVolume%()
	Local l.Lights
	
	If SecondaryLightOn > 0.3
		UpdateLightsTimer = UpdateLightsTimer + fps\Factor[0]
		If UpdateLightsTimer >= 8.0 Then UpdateLightsTimer = 0.0
		For l.Lights = Each Lights
			If l\room <> Null
				If l\room\Dist < 6.0 Lor l\room = PlayerRoom
					If UpdateLightsTimer = 0.0
						Local Dist# = EntityDistanceSquared(Camera, l\OBJ)
						
						If Dist < PowTwo(HideDistance) Then TempLightVolume = Max((TempLightVolume + PowTwo(l\Intensity) * ((HideDistance - Sqr(Dist)) / HideDistance)) / 4.5, 1.0)
					EndIf
				EndIf
			EndIf
		Next
		LightVolume = CurveValue(TempLightVolume, LightVolume, 50.0)
	Else
		LightVolume = 1.0
		UpdateLightsTimer = 0.0
	EndIf
End Function

Function UpdateLights%(Cam%)
	Local l.Lights, i%, Random#, Alpha#
	
	For l.Lights = Each Lights
		If SecondaryLightOn > 0.3
			If l\room <> Null
				If l\room\Dist < 6.0 Lor l\room = PlayerRoom
					If Cam = Camera ; ~ The lights are rendered by player's cam
						EntityOrder(l\AdvancedSprite, -1)
						If UpdateLightsTimer = 0.0
							Local Dist# = EntityDistanceSquared(Cam, l\OBJ)
							Local LightOBJHidden% = EntityHidden(l\OBJ)
							Local LightSpriteHidden% = EntityHidden(l\Sprite)
							Local LightAdvancedSpriteHidden% = EntityHidden(l\AdvancedSprite)
							
							If Dist < PowTwo(opt\CameraFogFar * LightVolume)
								EntityAutoFade(l\Sprite, 0.1 * LightVolume, opt\CameraFogFar * LightVolume)
								
								Local LightVisible% = EntityVisible(Cam, l\OBJ)
								Local LightInView% = EntityInView(l\OBJ, Cam)
								
								If LightOBJHidden Then ShowEntity(l\OBJ)
								If l\Flickers And Rand(13) = 1 And LightVisible And me\LightBlink =< 0.0
									PlaySound2(IntroSFX[Rand(8, 10)], Cam, l\OBJ, 4.0)
									If LightInView
										If (Not LightSpriteHidden) Then HideEntity(l\Sprite)
										If (Not  LightAdvancedSpriteHidden) Then HideEntity(l\AdvancedSprite)
									EndIf
									Random = Rnd(0.3, 0.7)
									SecondaryLightOn = Clamp(SecondaryLightOn - Random, 0.301, 1.0)
									TempLightVolume = Clamp(TempLightVolume - Random, 0.5, 1.0)
								EndIf
								If LightInView And LightVisible
									If LightSpriteHidden Then ShowEntity(l\Sprite)
									If opt\AdvancedRoomLights
										Alpha = 1.0 - Max(Min(((Sqr(Dist) + 0.5) / 7.5), 1.0), 0.0)
										If Alpha > 0.0
											If LightAdvancedSpriteHidden Then ShowEntity(l\AdvancedSprite)
											EntityAlpha(l\AdvancedSprite, Max(3.0 * (((CurrAmbientColorR + CurrAmbientColorG + CurrAmbientColorB) / 3) / 255.0) * (l\Intensity / 2.0), 1.0) * Alpha)
											
											Random = Rnd(0.36, 0.4)
											ScaleSprite(l\AdvancedSprite, Random, Random)
										Else
											; ~ Instead of rendering the sprite invisible, just hiding it if the player is far away from it
											If (Not LightAdvancedSpriteHidden) Then HideEntity(l\AdvancedSprite)
										EndIf
									Else
										; ~ The additional sprites option is disabled, hide the sprites
										If (Not LightAdvancedSpriteHidden) Then HideEntity(l\AdvancedSprite)
									EndIf
								Else
									; ~ Hide the sprites because they aren't visible
									If (Not LightSpriteHidden) Then HideEntity(l\Sprite)
									If (Not LightAdvancedSpriteHidden) Then HideEntity(l\AdvancedSprite)
								EndIf
							Else
								; ~ Hide the light emitter because it is too far
								If (Not LightOBJHidden) Then HideEntity(l\OBJ)
							EndIf
						EndIf
					Else
						; ~ This will make the lightsprites not glitch through the wall when they are rendered by the cameras
						EntityOrder(l\AdvancedSprite, 0)
					EndIf
				EndIf
			EndIf
		Else
			; ~ The lights were turned off
			If (Not EntityHidden(l\Sprite)) Then HideEntity(l\Sprite)
			If (Not EntityHidden(l\AdvancedSprite)) Then HideEntity(l\AdvancedSprite)
			If (Not EntityHidden(l\OBJ)) Then HideEntity(l\OBJ)
		EndIf
	Next
End Function

Function RemoveLight%(l.Lights)
	FreeEntity(l\Sprite) : l\Sprite = 0
	FreeEntity(l\AdvancedSprite) : l\AdvancedSprite = 0
	FreeEntity(l\OBJ) : l\OBJ = 0
	Delete(l)
End Function

Global AmbientLightRoomTex%

Function AmbientLightRooms%(R%, G%, B%)
	; ~ Save the current backbuffer
	Local OldBuffer% = BackBuffer() ; ~ Probably shouldn't make assumptions here but who cares, why wouldn't it use the BackBuffer()
	
	; ~ Change draw target to AmbientLightRoomTex
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	; ~ Clear color to provided values (R, G, B)
	ClsColor(R, G, B)
	Cls()
	; ~ Reset clear color to black (default)
	ClsColor(0, 0, 0)
	; ~ Restore the previous buffer
	SetBuffer(OldBuffer)
End Function

Const RoomScale# = 8.0 / 2048.0

;[Block]
Type AlarmRotors
	Field Obj%, GlassObj%
	Field Light%, LightCone%, Light2%, LightCone2%
	Field Speed#
	Field Sprite%, Sprite2%
	Field R#, G#, B#
	Field room.Rooms
End Type
;[End Block]

;[Block]
Const Alarm_Cone_X% = 0, Alarm_Cone_Y% = 1, Alarm_Cone_Z% = 3
;[End Block]

Function CreateAlarmRotor.AlarmRotors(room.Rooms, Name$, Speed#, TwoLights%, X#, Y#, Z#, Pitch#, Yaw#, Roll#, R#, G#, B#, ConeScX#, ConeScY#, ConeScZ#, LightConeRot# = Alarm_Cone_Y)
	Local ar.AlarmRotors = New AlarmRotors
	Local ConeX#, ConeY#, ConeZ#, LightAmount$
	
	; ~ Determining what axis should have light cone positioned
	;[Block]
	If LightConeRot = Alarm_Cone_X
		ConeX = (X - 30 * RoomScale)
		ConeY = Y
		ConeZ = Z
	ElseIf LightConeRot = Alarm_Cone_Y
		ConeX = X
		ConeY = (Y - 30 * RoomScale)
		ConeZ = Z
	Else
		ConeX = X
		ConeY = Y
		ConeZ = (Z - 30 * RoomScale)
	EndIf
	;[End Block]
	
	;[Block]
	If TwoLights
		LightAmount = "2"
	Else
		LightAmount = ""
	EndIf
	;[End Block]
	
	;[Block]
	ar\Obj = LoadMesh_Strict("GFX\Map\Props\" + Name + LightAmount + ".b3d")
	ScaleEntity(ar\Obj, RoomScale, RoomScale, RoomScale)
	PositionEntity(ar\Obj, X, Y, Z, True)
	RotateEntity(ar\Obj, Pitch, Yaw, Roll)
	EntityParent(ar\Obj, room\OBJ)
	;[End Block]
	
	;[Block]
	ar\GlassObj = LoadMesh_Strict("GFX\Map\Props\" + Name + "_Base.b3d")
	ScaleEntity(ar\GlassObj, RoomScale, RoomScale, RoomScale)
	PositionEntity(ar\GlassObj, X, Y, Z, True)
	RotateEntity(ar\GlassObj, Pitch, Yaw, Roll)
	EntityColor(ar\GlassObj, R * 3.0, G * 3.0, B * 3.0)
	EntityParent(ar\GlassObj, room\OBJ)
	;[End Block]
	
	;[Block]
	ar\Sprite = CreateSprite()
	Local tex = LoadTexture_Strict("GFX\Particles\light(2).png", 1 + 2)
	SpriteViewMode ar\Sprite, 2
	ScaleSprite ar\Sprite, 1.0, 1.0
	EntityFX ar\Sprite, 1
	EntityBlend ar\Sprite, 3
	RotateEntity ar\Sprite, Pitch, 0, 0
	EntityTexture ar\Sprite, tex
	EntityColor(ar\Sprite, R, G, B)
	EntityParent(ar\Sprite, ar\GlassObj)
	DeleteSingleTextureEntryFromCache tex
	HideEntity ar\Sprite
	;[End Block]
	
	;[Block]
	ar\Light = CreateLight(3, ar\Obj)
	MoveEntity(ar\Light, 0, 0, 0.001)
	LightRange(ar\Light, 1.5)
	LightColor(ar\Light, R * 3.0, G * 3.0, B * 3.0)
	LightConeAngles(ar\Light, 0, 75)
	HideEntity(ar\Light)
	;[End Block]
	
	;[Block]
	ar\LightCone = CopyEntity(misc_I\LightCone)
	ScaleEntity(ar\LightCone, ConeScX, ConeScY, ConeScZ)
	RotateEntity(ar\LightCone, Pitch + 90.0, Yaw, Roll + 180.0)
	EntityAlpha(ar\LightCone, 0.15)
	EntityBlend(ar\LightCone, 3)
;	EntityFX(ar\LightCone, 2)
	EntityColor(ar\LightCone, R * 3.0, G * 3.0, B * 3.0)
	EntityParent(ar\LightCone, ar\Obj)
	PositionEntity(ar\LightCone, ConeX, ConeY, ConeZ, True)
	HideEntity(ar\LightCone)
	;[End Block]
	
	;[Block]
	If TwoLights
		ar\Light2 = CreateLight(3, ar\Obj)
		MoveEntity(ar\Light2, 0, 0, -0.001)
		LightRange(ar\Light2, 1.5)
		LightColor(ar\Light2, R * 3.0, G * 3.0, B * 3.0)
		RotateEntity(ar\Light2, EntityPitch(ar\Light2), EntityYaw(ar\Light2) + 180, EntityRoll(ar\Light2))
		LightConeAngles(ar\Light2, 0, 75)
		HideEntity(ar\Light2)
		
		ar\LightCone2 = CopyMesh(ar\LightCone)
		ScaleEntity(ar\LightCone2, ConeScX, ConeScY, ConeScZ)
		RotateEntity(ar\LightCone2, Pitch + 90.0, Yaw, Roll)
		EntityAlpha(ar\LightCone2, 0.15)
		EntityBlend(ar\LightCone2, 3)
;		EntityFX(ar\LightCone2, 2)
		EntityColor(ar\LightCone2, R * 3.0, G * 3.0, B * 3.0)
		EntityParent(ar\LightCone2, ar\Obj)
		PositionEntity(ar\LightCone2, ConeX, ConeY, ConeZ, True)
		HideEntity(ar\LightCone2)
	EndIf
	;[End Block]
	
	;[Block]
	ar\Speed = Speed
	;[End Block]
	
	Return(ar)
End Function

Function UpdateAlarmRotor(ar.AlarmRotors)
	
	;[Block]
	ShowEntity(ar\Sprite)
	ShowEntity(ar\Light) : ShowEntity(ar\LightCone)
	If ar\Light2 <> 0 Then ShowEntity(ar\Light2)
	If ar\LightCone2 <> 0 Then ShowEntity(ar\LightCone2)
	;[End Block]
	
	;[Block]
	TurnEntity(ar\Obj, 0 , ar\Speed * fps\Factor[0], 0)
	;[End Block]
	
End Function

; ~ Flu Light Constants
;[Block]
Const MaxFluTextures = 3
Const MaxFluSounds = 7
Const FluState_Off=0
Const FluState_Between=1
Const FluState_On=2
Const FLU_STATE_OFF=0
Const FLU_STATE_ON=1
Const FLU_STATE_FLICKER=2
;[End Block]

;[Block]
Type FluLights
	Field ID%
	Field Obj%
	Field Tex%[MaxFluTextures]
	Field Time#
	Field Sfx%[MaxFluSounds]
	Field FlashSprite%
	Field LightObj%
	Field State%
	Field room.Rooms
End Type
;[End Block]

Function CreateFluLight.FluLights(room.Rooms, Obj$, Id%, R#, G#, B#, X#, Y#, Z#, Pitch# = 0.0, Yaw# = 0.0, Roll# = 0.0)
	Local fll.FluLights = New FluLights
	Local i%
	
	fll\ID = Id
	
	fll\Obj = LoadMesh_Strict("GFX\Map\Props\"+(Obj$)+".b3d")
	ScaleEntity fll\Obj, RoomScale, RoomScale, RoomScale
	
	If fll\Tex[FluState_Off] = 0
		For i = 0 To MaxFluTextures-1
			fll\Tex[i] = LoadTexture("GFX\Map\Textures\"+(Obj$)+" ("+(i+1)+").png", 1)
		Next
	EndIf
	EntityTexture fll\Obj, fll\Tex[FluState_Off]
	HideEntity fll\Obj
	
	If fll\Sfx[0] = 0
		For i = 0 To MaxFluSounds-1
			fll\Sfx[i] = LoadSound_Strict("SFX\Room\Flu_Lights\Flu_Light ("+(i+1)+").ogg")
		Next
	EndIf
	
	fll\FlashSprite = CreateSprite()
	Local tex = LoadTexture_Strict("GFX\Particles\light(2).png", 1+2)
	SpriteViewMode fll\FlashSprite, 2
	ScaleSprite fll\FlashSprite, 1.0, 1.0
	EntityFX fll\FlashSprite, 1
	EntityBlend fll\FlashSprite, 3
	RotateEntity fll\FlashSprite, -90, 0, 0
	EntityTexture fll\FlashSprite, tex
	EntityColor(fll\FlashSprite, R, G, B)
	DeleteSingleTextureEntryFromCache tex
	HideEntity fll\FlashSprite
	
	fll\LightObj = CreateLight(2)
	LightColor(fll\LightObj, R * 5.0, G * 5.0, B * 5.0)
	LightRange(fll\LightObj, 0.025)
	
	PositionEntity fll\Obj, X, Y, Z
	RotateEntity fll\Obj, Pitch, Yaw, Roll
	EntityPickMode fll\Obj, 2
	EntityParent fll\Obj, room\OBJ
	PositionEntity fll\LightObj, X, Y, Z
	EntityParent fll\LightObj, fll\Obj
	PositionEntity fll\FlashSprite, X, Y - 0.07, Z
	EntityParent fll\FlashSprite, fll\Obj
	fll\room = room
	
	Return(fll)
End Function

Function UpdateFluLights()
	Local fll.FluLights
	
	For fll = Each FluLights
		If fll\room = PlayerRoom Lor IsRoomAdjacent(fll\room, PlayerRoom) Then
			ShowEntity fll\Obj
			Select(fll\State)
				Case FLU_STATE_OFF
					EntityFX fll\Obj, 0
					HideEntity fll\FlashSprite
					HideEntity fll\LightObj
					EntityTexture fll\Obj, fll\Tex[FluState_Off]
				Case FLU_STATE_ON
					EntityFX fll\Obj, 1
					ShowEntity fll\FlashSprite
					ShowEntity fll\LightObj
					EntityTexture fll\Obj, fll\Tex[FluState_On]
				Case FLU_STATE_FLICKER
					If fll\Time = 0.0 Then
						EntityFX fll\Obj, 0
						HideEntity fll\FlashSprite
						HideEntity fll\LightObj
						EntityTexture fll\Obj, fll\Tex[FluState_Off]
						If Rand(100) = 1 Then
							fll\Time = fps\Factor[0]
							PlaySound2(fll\Sfx[Rand(0, MaxFluSounds - 4)], Camera, fll\Obj)
						EndIf
					ElseIf fll\Time > 0.0 Then
						EntityFX fll\Obj, 0
						HideEntity fll\FlashSprite
						HideEntity fll\LightObj
						EntityTexture fll\Obj, fll\Tex[FluState_Between]
						fll\Time = fll\Time + fps\Factor[0]
						If fll\Time > 70*Rnd(1.0, 3.0) Then
							fll\Time = -70*0.2
							PlaySound2(fll\Sfx[Rand(4, MaxFluSounds - 1)], Camera, fll\Obj)
						EndIf
					Else
						EntityFX fll\Obj, 1
						ShowEntity fll\FlashSprite
						ShowEntity fll\LightObj
						EntityTexture fll\Obj, fll\Tex[FluState_On]
						fll\Time = Min(fll\Time + fps\Factor[0], 0.0)
					EndIf
			End Select
		Else
			HideEntity fll\Obj
		EndIf
	Next
	
End Function

Function InitFluLight(room.Rooms, ID%, State%)
	Local fll.FluLights
	
	For fll = Each FluLights
		If fll\room = room Then
			If fll\ID = ID Then
				fll\State = State
			EndIf
		EndIf
	Next
	
End Function

;[Block]
Type Projectors
	Field Room.Rooms
	Field Obj%, ObjCopy%
	Field Texture%, TextureBl%
	Field PickEntity%
	Field ShouldMove%
	Field Timer#, FlickerTimer#
	Field InitialX#, InitialRoll#
End Type
;[End Block]

Function CreateProjector.Projectors(room.Rooms, Obj$ = "GFX\Map\Rooms\projector_wall.b3d", X#, Y#, Z#, Pitch#, Yaw#, Roll#, Texture$, TextureX#, TextureY#, TextureZ#, TexturePitch#, TextureYaw#, TextureRoll#)
	Local proj.Projectors = New Projectors
	
	proj\Room = room
	
	; ~ Creating Object
	;[Block]
	proj\Obj = LoadMesh_Strict(Obj)
	PositionEntity(proj\Obj, X, Y, Z)
	RotateEntity(proj\Obj, Pitch, Yaw, Roll)
	ScaleEntity(proj\Obj, RoomScale, RoomScale, RoomScale)
	;[End Block]
	
	; ~ Creating Other Stuff
	;[Block]
	proj\ObjCopy = CopyMesh(proj\Obj)
	proj\TextureBl = CreateTexture(1, 1)
	SetBuffer(TextureBuffer(proj\TextureBl))
	ClsColor(255, 255, 255)
	Cls()
	SetBuffer(BackBuffer())
	ClsColor(0, 0, 0)
	PositionEntity(proj\ObjCopy, X, Y, Z)
	RotateEntity(proj\ObjCopy, Pitch, Yaw, Roll)
	ScaleEntity(proj\ObjCopy, RoomScale, RoomScale, RoomScale)
	EntityBlend(proj\ObjCopy, 3)
	EntityTexture(proj\ObjCopy, proj\TextureBl)
	FreeTexture(proj\TextureBl)
	;[End Block]
	
	; ~ Loading Texture
	;[Block]
	proj\Texture = LoadTexture(Texture, 1 + 2 + 16 + 32)
	TextureBlend(proj\Texture, 2)
	;[End Block]
	
	; ~ Texturing The Object
	;[Block]
	EntityTexture(proj\ObjCopy, proj\Texture, 0, 1)
	;[End Block]
	
	; ~ Creating Pick Pivot For The Texture To Render
	;[Block]
	proj\PickEntity = CreatePivot()
	PositionEntity(proj\PickEntity, X + TextureX, Y + TextureY, Z + TextureZ)
	TurnEntity(proj\PickEntity, TexturePitch, TextureYaw, TextureRoll)
;	MoveEntity(proj\PickEntity, TextureX, TextureY, TextureZ)
	;[End Block]
	
	; ~ Applying Some Variables
	;[Block]
	proj\ShouldMove = False
	proj\Timer = 0.0 : proj\FlickerTimer = 0.0
	proj\InitialX = EntityX(proj\PickEntity) : proj\InitialRoll = EntityRoll(proj\PickEntity)
	;[End Block]
	
	Return(proj)
End Function

Function UpdateProjector(proj.Projectors, Scale# = 0.8, MovementSpeed# = 0.0)
	Local Surf%, s%, v%
	Local Random#
	
	; ~ Determining If The Texture Will Rotate Or Not
	;[Block]
	If MovementSpeed > 0.0 Then
		proj\ShouldMove = True
	Else
		proj\ShouldMove = False
	EndIf
	;[End Block]
	
	; ~ Rotation Logic
	;[Block]
	If proj\ShouldMove Then
		proj\Timer = proj\Timer + MovementSpeed#
		RotateEntity proj\PickEntity, EntityPitch(proj\PickEntity), EntityYaw(proj\PickEntity), proj\InitialRoll + Sin(proj\Timer) * 10.0
		PositionEntity proj\PickEntity, proj\InitialX - Sin(proj\Timer) * 0.2, EntityY(proj\PickEntity), EntityZ(proj\PickEntity)
	Else
		Random# = Rnd(-0.01, 0.01)
		RotateEntity proj\PickEntity, EntityPitch(proj\PickEntity), EntityYaw(proj\PickEntity), proj\InitialRoll
		PositionEntity proj\PickEntity, proj\InitialX + Random, EntityY(proj\PickEntity), EntityZ(proj\PickEntity)
	EndIf
	;[End Block]
	
	; ~ Flickering Logic
	;[Block]
	If Rand(1000) = 1000 Then
		proj\FlickerTimer = 100
	EndIf
	
	If proj\FlickerTimer > 0 Then
		proj\FlickerTimer = Max(proj\FlickerTimer - 1, 0)
	EndIf
	
	If (proj\FlickerTimer Mod 5.0) < 0.5 Then
		ShowEntity proj\ObjCopy
	Else
		HideEntity proj\ObjCopy
	EndIf
	;[End Block]
	
	; ~ Rendering The Texture
	;[Block]
	For s = 1 To CountSurfaces(proj\ObjCopy)
		Surf = GetSurface(proj\ObjCopy, s)
		For v = 0 To CountVertices(Surf) - 1
			TFormPoint(VertexX(Surf,v), VertexY(Surf,v), VertexZ(Surf,v), proj\ObjCopy, proj\PickEntity)
			VertexTexCoords(Surf, v, (TFormedX() / Scale) + 0.5, 1 - ((TFormedY() / Scale) + 0.5))
		Next 
	Next
	;[End Block]
End Function

Function RemoveProjector%(proj.Projectors)
	If proj\Obj <> 0 Then FreeEntity(proj\Obj) : proj\Obj = 0
	If proj\ObjCopy <> 0 Then FreeEntity(proj\ObjCopy) : proj\ObjCopy = 0
	If proj\Texture <> 0 Then FreeTexture(proj\Texture) : proj\Texture = 0
	If proj\TextureBl <> 0 Then FreeTexture(proj\TextureBl) : proj\TextureBl = 0
	Delete(proj)
End Function

; ~ Cube Map Constants
;[Block]
Const Vector_X = 0
Const Vector_Y = 1
Const Vector_Z = 2
Const Rotator_Pitch = 0
Const Rotator_Yaw = 1
Const Rotator_Roll = 2
;[End Block]

; ~ Cube Map Type
;[Block]
Type CubeMap
	Field Name$
	Field Texture%
	Field Cam%
	Field CamOverlay%
	Field RenderTimer%
	Field RenderY#
	Field Position#[3]
	Field FollowsCamera%
End Type
;[End Block]

Global MapCubeMap.CubeMap

; ~ Creating Cube Map
;[Block]
Function CreateCubeMap.CubeMap(Name$, CubeMapMode% = 1, FollowsCamera% = True, PosX# = 0.0, PosY# = 0.0, PosZ# = 0.0, RenderY# = 0.0, TexSize% = 256)
	Local cm.CubeMap = New CubeMap
	
	cm\Name = Name$
	cm\RenderY = RenderY#
	
	cm\Texture = CreateTextureUsingCacheSystem(TexSize, TexSize, 128 + 256)
	TextureBlend(cm\Texture, 3)
	cm\Cam = CreateCamera()
	CameraFogMode cm\Cam, 1
	CameraFogRange cm\Cam, 5, 10
	CameraRange cm\Cam, 0.01, 20
	CameraClsMode cm\Cam, False, True
	CameraProjMode cm\Cam, 0
	CameraViewport cm\Cam, 0, 0, TexSize, TexSize
	cm\CamOverlay = CreateSprite(cm\Cam)
	EntityFX cm\CamOverlay, 1
	EntityColor cm\CamOverlay, 0, 0, 0
	EntityOrder cm\CamOverlay, -1000
	ScaleSprite cm\CamOverlay, 5, 5
	MoveEntity cm\CamOverlay, 0, 0, 0.1
	EntityAlpha cm\CamOverlay, 0.0
	HideEntity cm\CamOverlay
	
	SetCubeMode cm\Texture, CubeMapMode
	
	cm\FollowsCamera = FollowsCamera
	If cm\FollowsCamera = False
		cm\Position[Vector_X] = PosX#
		cm\Position[Vector_Y] = PosY#
		cm\Position[Vector_Z] = PosZ#
	EndIf
	
	Return(cm)
End Function
;[End Block]

; ~ Rendering Cube Map
;[Block]
Function RenderCubeMap(Entity%, Name$)
	Local cm.CubeMap
	Local tex_sz%
	
	If opt\RenderCubeMapMode = 0 Then Return
	
	For cm = Each CubeMap
		If cm\Name = Name$
			tex_sz = TextureWidth(cm\Texture)
			CameraProjMode Camera,0
			CameraProjMode cm\Cam,1
			If Entity <> 0
				HideEntity Entity
			EndIf
			HideEntity wep_I\Pivot
			ShowEntity me\ModelHead
			ShowEntity cm\CamOverlay
			EntityAlpha cm\CamOverlay,0.7
			CameraFogRange cm\Cam,0.1,3
			CameraFogColor cm\Cam,5,20,3
			If opt\RenderCubeMapMode = 2
				;do left view
				SetCubeFace cm\Texture,0
				RotateEntity cm\Cam,0,90,0
				RenderWorld
				CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
				;do forward view
				SetCubeFace cm\Texture,1
				RotateEntity cm\Cam,0,0,0
				RenderWorld
				CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
				;do right view	
				SetCubeFace cm\Texture,2
				RotateEntity cm\Cam,0,-90,0
				RenderWorld
				CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
				;do backward view
				SetCubeFace cm\Texture,3
				RotateEntity cm\Cam,0,180,0
				RenderWorld
				CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
				;do up view
				SetCubeFace cm\Texture,4
				RotateEntity cm\Cam,-90,0,0
				RenderWorld
				CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
				;do down view
				SetCubeFace cm\Texture,5
				RotateEntity cm\Cam,90,0,0
				RenderWorld
				CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
			ElseIf opt\RenderCubeMapMode = 1
				If cm\RenderTimer <= 0 Then
					;do left view	
					SetCubeFace cm\Texture,0
					RotateEntity cm\Cam,0,90,0
					RenderWorld
					CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
					;do forward view
					SetCubeFace cm\Texture,1
					RotateEntity cm\Cam,0,0,0
					RenderWorld
					CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
					cm\RenderTimer = 1
				ElseIf cm\RenderTimer = 1
					;do right view	
					SetCubeFace cm\Texture,2
					RotateEntity cm\Cam,0,-90,0
					RenderWorld
					CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
					;do backward view
					SetCubeFace cm\Texture,3
					RotateEntity cm\Cam,0,180,0
					RenderWorld
					CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
					cm\RenderTimer = 2
				ElseIf cm\RenderTimer = 2
					;do up view
					SetCubeFace cm\Texture,4
					RotateEntity cm\Cam,-90,0,0
					RenderWorld
					CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
					;do down view
					SetCubeFace cm\Texture,5
					RotateEntity cm\Cam,90,0,0
					RenderWorld
					CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(cm\Texture)
					cm\RenderTimer = 0
				EndIf
			EndIf	
			If Entity <> 0
				ShowEntity Entity
			EndIf
			ShowEntity wep_I\Pivot
			HideEntity me\ModelHead
			EntityAlpha cm\CamOverlay, 0.0
			CameraFogRange cm\Cam, 5, 10
			CameraFogColor cm\Cam, 0, 0, 0
			HideEntity cm\CamOverlay
			CameraProjMode Camera, 1
			CameraProjMode cm\Cam, 0
			Exit
		EndIf
	Next
	
End Function
;[End Block]

; ~ Updating Cube Map
;[Block]
Function UpdateCubeMap(Entity%, Name$)
	Local cm.CubeMap
	Local camoffsety#
	
	If opt\RenderCubeMapMode = 0 Then Return
	
	For cm = Each CubeMap
		If cm\Name = Name$
			If cm\FollowsCamera
				If Entity <> 0
					camoffsety#=(EntityY(Camera,True)-(EntityY(Entity,True)+cm\RenderY))
					PositionEntity cm\Cam,EntityX(Camera,True),(EntityY#(Entity,True)+cm\RenderY)-camoffsety,EntityZ(Camera,True)
				Else
					camoffsety#=(EntityY(Camera,True)+cm\RenderY)
					PositionEntity cm\Cam,EntityX(Camera,True),cm\RenderY-camoffsety,EntityZ(Camera,True)
				EndIf
			Else
				PositionEntity cm\Cam,cm\Position[Vector_X],cm\Position[Vector_Y],cm\Position[Vector_Z]
			EndIf
			If Entity <> 0
				ShowEntity(Entity)
			EndIf
			Exit
		EndIf
	Next
	
End Function
;[End Block]

; ~ Water Instance Type
;[Block]
Type WaterInstance
	Field UpdateWaterString$
	Field IgnoreOBJ%
End Type
;[End Block]

Global wa_I.WaterInstance = New WaterInstance

; ~ Main Water Type
;[Block]
Type Water
	Field OBJ%
	Field VexX#[256]
	Field VexY#[256]
	Field VexZ#[256]
	Field PrevVexY#[256]
	Field Name$
	Field Timer#
	Field IsRendering%
	Field CustomY#
End Type
;[End Block]

; ~ Creating Water
;[Block]
Function CreateWater.Water(File$, Name$, x#, y#, z#, Parent% = 0, CustomY# = 0.0)
	Local wa.Water = New Water
	Local Surf%, i%
	
	wa\OBJ = LoadMesh_Strict(File,Parent)
	PositionEntity wa\OBJ,x,y,z
	wa\Name = Name
	
	Surf = GetSurface(wa\OBJ, 1)
	
	For i = 0 To CountVertices(Surf) - 1
		wa\VexX#[i]=VertexX#(Surf,i)
		wa\VexY#[i]=VertexY#(Surf,i)
		wa\VexZ#[i]=VertexZ#(Surf,i)
		wa\PrevVexY#[i]=wa\VexY#[i]
	Next
	
	EntityTexture wa\OBJ, MapCubeMap\Texture, 0, 1
	wa\CustomY = CustomY
	
	Return(wa)
End Function
;[End Block]

; ~ Updating Water
;[Block]
Function UpdateWater(Name$)
	Local wa.Water, it.Items
	
	For wa = Each Water
		wa\IsRendering = False
		If wa\Name = Name
			wa\IsRendering = True
			wa\Timer = wa\Timer + 2 * fps\Factor[0]
			UpdateCubeMap(wa\OBJ, "MapCubeMap")
		EndIf
	Next
End Function
;[End Block]

; ~ Rendering Water
;[Block]
Function RenderWater(Name$)
	Local wa.Water, it.Items
	Local i%, s%
	
	For wa = Each Water
		If wa\Name = Name
			For it = Each Items
				If EntityY(it\Collider) < wa\CustomY
					HideEntity(it\Model)
				EndIf
			Next
			If wa_I\IgnoreOBJ <> 0
				HideEntity wa_I\IgnoreOBJ
			EndIf
			
			MapCubeMap\RenderY = wa\CustomY
			
			s = GetSurface(wa\OBJ, 1)
			For i = 0 To CountVertices(s) - 1
				wa\VexY[i] = Sin(wa\Timer + wa\VexX[i] * 500 + wa\VexZ[i] * 300) * 5.0
				VertexCoords(s, i, wa\VexX[i], wa\PrevVexY[i] - wa\VexY[i], wa\VexZ[i])
			Next
			
			UpdateNormals(wa\OBJ)
			RenderCubeMap(wa\OBJ, "MapCubeMap")
			
			If wa_I\IgnoreOBJ <> 0
				ShowEntity wa_I\IgnoreOBJ
			EndIf
			
			For it = Each Items
				If EntityY(it\Collider) < wa\CustomY
					ShowEntity(it\Model)
				EndIf
			Next
		EndIf
	Next
	
End Function
;[End Block]

;[Block]
Type SoundEmitters
	Field OBJ%
	Field ID%
	Field Range#
	Field SoundCHN%
	Field room.Rooms
End Type
;[End Block]

;[Block]
Type TempSoundEmitters
	Field x#, y#, z#
	Field ID%
	Field Range#
	Field RoomTemplate.RoomTemplates
End Type
;[End Block]

Function CreateSoundEmitter.SoundEmitters(room.Rooms, ID%, x#, y#, z#, Range#)
	Local se.SoundEmitters
	
	se.SoundEmitters = New SoundEmitters
	se\room = room
	
	se\OBJ = CreatePivot()
	PositionEntity(se\OBJ, x, y, z)
	If room <> Null Then EntityParent(se\OBJ, room\OBJ)
	
	se\ID = ID
	se\Range = Range
	
	Return(se)
End Function

Function UpdateSoundEmitters%()
	Local se.SoundEmitters
	Local i%
	
	For se.SoundEmitters = Each SoundEmitters
		If se\room <> Null
			If se\room\Dist < 6.0 Lor se\room = PlayerRoom
				If EntityDistanceSquared(se\OBJ, me\Collider) < PowTwo(se\Range) Then se\SoundCHN = LoopSound2(RoomAmbience[se\ID - 1], se\SoundCHN, Camera, se\OBJ, se\Range)
			EndIf
		EndIf
	Next
End Function

Function RemoveSoundEmitter%(se.SoundEmitters)
	FreeEntity(se\OBJ) : se\OBJ = 0
	Delete(se)
End Function

Function LoadRMesh%(File$, rt.RoomTemplates, HasCollision% = True)
	CatchErrors("LoadRMesh(" + File + ")")
	
	Local mat.Materials
	
	ClsColor(0, 0, 0)
	
	Local i%, j%, k%, x#, y#, z#
	Local Vertex%
	Local Temp1i% = 0, Temp2i% = 0, Temp3i% = 0
	Local Temp1s$
	Local CollisionMeshes% = CreatePivot()
	;Local HasTriggerBox% = False
	; ~ Read the file
	Local f% = ReadFile_Strict(File)
	
	If f = 0 Then RuntimeError(Format(GetLocalString("runerr", "file"), File))
	
	Local IsRMesh$ = ReadString(f)
	
	If IsRMesh = "RoomMesh"
		; ~ Continue
	;ElseIf IsRMesh = "RoomMesh.HasTriggerBox"
	;	HasTriggerBox = True
	Else
		RuntimeError(Format(Format(GetLocalString("runerr", "notrmesh"), File, "{0}"), IsRMesh, "{1}"))
	EndIf
	
	Local FilePath$ = StripFileName(File)
	
	Local Count%, Count2%
	
	; ~ Drawn meshes
	Local Opaque%, Alpha%
	
	Opaque = CreateMesh()
	Alpha = CreateMesh()
	
	Local ChildMesh%
	Local Surf%, Tex%[2], Brush%
	Local IsAlpha%
	Local u#, v#
	
	Count = ReadInt(f)
	
	For i = 1 To Count ; ~ Drawn mesh
		ChildMesh = CreateMesh()
		
		Surf = CreateSurface(ChildMesh)
		
		Brush = CreateBrush()
		
		Tex[0] = 0 : Tex[1] = 0
		
		IsAlpha = 0
		
		For j = 0 To 1
			Temp1i = ReadByte(f)
			If Temp1i <> 0
				Temp1s = ReadString(f)
				If FileType(FilePath + Temp1s) = 1 ; ~ Check if texture is existing in original path
					If Temp1i < 3
						If Instr(Temp1s, "_lm") <> 0
							Tex[j] = LoadTextureCheckingIfInCache(FilePath + Temp1s, 1 + 256)
						Else
							Tex[j] = LoadTextureCheckingIfInCache(FilePath + Temp1s)
						EndIf
					Else
						Tex[j] = LoadTextureCheckingIfInCache(FilePath + Temp1s, 3)
					EndIf
				ElseIf FileType(MapTexturesFolder + Temp1s) = 1 ; ~ If not, check the MapTexturesFolder
					If Temp1i < 3
						If Instr(Temp1s, "_lm") <> 0
							Tex[j] = LoadTextureCheckingIfInCache(MapTexturesFolder + Temp1s, 1 + 256)
						Else
							Tex[j] = LoadTextureCheckingIfInCache(MapTexturesFolder + Temp1s)
						EndIf
					Else
						Tex[j] = LoadTextureCheckingIfInCache(MapTexturesFolder + Temp1s, 3)
					EndIf
				EndIf
				If Tex[j] <> 0
					If Temp1i = 1 Then TextureBlend(Tex[j], 2 + (3 * opt\Atmosphere))
					If Instr(Lower(Temp1s), "_lm") <> 0 Then TextureBlend(Tex[j], 3 - (Not opt\Atmosphere))
					IsAlpha = 2
					If Temp1i = 3 Then IsAlpha = 1
					TextureCoords(Tex[j], 1 - j)
				EndIf
			EndIf
		Next
		
		If IsAlpha = 1
			If Tex[1] <> 0
				TextureBlend(Tex[1], 2)
				BrushTexture(Brush, Tex[1], 0, 0)
			Else
				BrushTexture(Brush, MissingTexture, 0, 0)
			EndIf
		Else
			Local BumpTex% = 0
			
			If Tex[0] <> 0 And Tex[1] <> 0
				If opt\BumpEnabled
					Local Temp$ = StripPath(TextureName(Tex[1]))
					
					For mat.Materials = Each Materials
						If mat\Name = Temp
							BumpTex = mat\Bump
							Exit
						EndIf
					Next
				Else
					BumpTex = 0
				EndIf
				If BumpTex = 0
					For j = 0 To 1
						BrushTexture(Brush, Tex[j], 0, j + 1)
					Next
				Else
					TextureCoords(BumpTex, 0)
					For j = 0 To 1
						BrushTexture(Brush, Tex[j], 0, j + 2)
					Next
					BrushTexture(Brush, BumpTex, 0, 1)
				EndIf
				BrushTexture(Brush, AmbientLightRoomTex, 0)
			Else
				If opt\BumpEnabled
					If Tex[1] <> 0
						Temp = StripPath(TextureName(Tex[1]))
						For mat.Materials = Each Materials
							If mat\Name = Temp
								BumpTex = mat\Bump
								Exit
							EndIf
						Next
					EndIf
				Else
					BumpTex = 0
				EndIf
				If BumpTex = 0
					For j = 0 To 1
						If Tex[j] <> 0
							BrushTexture(Brush, Tex[j], 0, j)
						Else
							BrushTexture(Brush, MissingTexture, 0, j)
						EndIf
					Next
				Else
					TextureCoords(BumpTex, 0)
					For j = 0 To 1
						If Tex[j] <> 0
							BrushTexture(Brush, Tex[j], 0, j + 1)
						Else
							BrushTexture(Brush, MissingTexture, 0, j + 1)
						EndIf
					Next
					BrushTexture(Brush, BumpTex, 0, 0)
				EndIf
			EndIf
		EndIf
		
		Surf = CreateSurface(ChildMesh) ; ~ Check if this don't needed anymore
		
		If IsAlpha > 0 Then PaintSurface(Surf, Brush)
		
		FreeBrush(Brush) : Brush = 0
		
		Count2 = ReadInt(f) ; ~ Vertices
		
		For j = 1 To Count2
			; ~ World coords
			x = ReadFloat(f) : y = ReadFloat(f) : z = ReadFloat(f)
			Vertex = AddVertex(Surf, x, y, z)
			
			; ~ Texture coords
			For k = 0 To 1
				u = ReadFloat(f) : v = ReadFloat(f)
				VertexTexCoords(Surf, Vertex, u, v, 0.0, k)
			Next
			
			; ~ Colors
			Temp1i = ReadByte(f)
			Temp2i = ReadByte(f)
			Temp3i = ReadByte(f)
			VertexColor(Surf, Vertex, Temp1i, Temp2i, Temp3i, 1.0)
		Next
		
		Count2 = ReadInt(f) ; ~ Polys
		For j = 1 To Count2
			Temp1i = ReadInt(f) : Temp2i = ReadInt(f) : Temp3i = ReadInt(f)
			AddTriangle(Surf, Temp1i, Temp2i, Temp3i)
		Next
		
		If IsAlpha = 1
			AddMesh(ChildMesh, Alpha)
			EntityAlpha(ChildMesh, 0.0)
		Else
			AddMesh(ChildMesh, Opaque)
			EntityParent(ChildMesh, CollisionMeshes)
			EntityAlpha(ChildMesh, 0.0)
			If HasCollision
				EntityType(ChildMesh, HIT_MAP)
			Else
				EntityType(ChildMesh, 0)
			EndIf
			EntityPickMode(ChildMesh, 2)
			
			; ~ Make collision double-sided
			If HasCollision
				Local FlipChild% = CopyMesh(ChildMesh)
				
				FlipMesh(FlipChild)
				AddMesh(FlipChild, ChildMesh)
				FreeEntity(FlipChild) : FlipChild = 0
			EndIf
		EndIf
	Next
	
	Local HiddenMesh%
	
	HiddenMesh = CreateMesh()
	
	Count = ReadInt(f) ; ~ Invisible collision mesh
	For i = 1 To Count
		Surf = CreateSurface(HiddenMesh)
		Count2 = ReadInt(f) ; ~ Vertices
		For j = 1 To Count2
			; ~ World coords
			x = ReadFloat(f) : y = ReadFloat(f) : z = ReadFloat(f)
			Vertex = AddVertex(Surf, x, y, z)
		Next
		
		Count2 = ReadInt(f) ; ~ Polys
		For j = 1 To Count2
			Temp1i = ReadInt(f) : Temp2i = ReadInt(f) : Temp3i = ReadInt(f)
			AddTriangle(Surf, Temp1i, Temp2i, Temp3i)
			AddTriangle(Surf, Temp1i, Temp3i, Temp2i)
		Next
	Next
	
	; ~ Trigger boxes
	;If HasTriggerBox
	;	Local TB%
	;	
	;	rt\TempTriggerBoxAmount = ReadInt(f)
	;	For TB = 0 To rt\TempTriggerBoxAmount - 1
	;		rt\TempTriggerBox[TB] = CreateMesh(rt\OBJ)
	;		Count = ReadInt(f)
	;		For i = 1 To Count
	;			Surf = CreateSurface(rt\TempTriggerBox[TB])
	;			Count2 = ReadInt(f)
	;			For j = 1 To Count2
	;				x = ReadFloat(f) : y = ReadFloat(f) : z = ReadFloat(f)
	;				Vertex = AddVertex(Surf, x, y, z)
	;			Next
	;			Count2 = ReadInt(f)
	;			For j = 1 To Count2
	;				Temp1i = ReadInt(f) : Temp2i = ReadInt(f) : Temp3i = ReadInt(f)
	;				AddTriangle(Surf, Temp1i, Temp2i, Temp3i)
	;				AddTriangle(Surf, Temp1i, Temp3i, Temp2i)
	;			Next
	;		Next
	;		rt\TempTriggerBoxName[TB] = ReadString(f)
	;	Next
	;EndIf
	
	Count = ReadInt(f) ; ~ Point entities
	
	Local ts.TempScreens, twp.TempWayPoints, tl.TempLights, tse.TempSoundEmitters, tp.TempProps
	Local Range#, lColor$, Intensity#
	Local R%, G%, B%
	Local Angles$
	Local Temp2s$
	
	If rt <> Null ; ~ TEMPORARY SOLUTION
		For i = 1 To Count
			Temp1s = ReadString(f)
			Select(Temp1s)
				Case "screen"
					;[Block]
					ts.TempScreens = New TempScreens
					ts\RoomTemplate = rt
					
					ts\x = ReadFloat(f) * RoomScale
					ts\y = ReadFloat(f) * RoomScale
					ts\z = ReadFloat(f) * RoomScale
					
					Temp2s = ReadString(f)
					ts\ImgPath = Temp2s
					;[End Block]
				Case "waypoint"
					;[Block]
					twp.TempWayPoints = New TempWayPoints
					twp\RoomTemplate = rt
					
					twp\x = ReadFloat(f) * RoomScale
					twp\y = ReadFloat(f) * RoomScale
					twp\z = ReadFloat(f) * RoomScale
					;[End Block]
				Case "light"
					;[Block]
					tl.TempLights = New TempLights
					tl\RoomTemplate = rt
					
					tl\x = ReadFloat(f) * RoomScale
					tl\y = ReadFloat(f) * RoomScale
					tl\z = ReadFloat(f) * RoomScale
					tl\lType = 2
					tl\Range = ReadFloat(f) / 2000.0
					
					lColor = ReadString(f)
					Intensity = Min(ReadFloat(f) * 0.8, 1.0)
					tl\R = Int(Piece(lColor, 1, " ")) * Intensity
					tl\G = Int(Piece(lColor, 2, " ")) * Intensity
					tl\B = Int(Piece(lColor, 3, " ")) * Intensity
					;[End Block]
				Case "spotlight"
					;[Block]
					tl.TempLights = New TempLights
					tl\RoomTemplate = rt
					
					tl\x = ReadFloat(f) * RoomScale
					tl\y = ReadFloat(f) * RoomScale
					tl\z = ReadFloat(f) * RoomScale
					tl\lType = 3
					tl\Range = ReadFloat(f) / 2000.0
					
					lColor = ReadString(f)
					Intensity = Min(ReadFloat(f) * 0.8, 1.0)
					tl\R = Int(Piece(lColor, 1, " ")) * Intensity
					tl\G = Int(Piece(lColor, 2, " ")) * Intensity
					tl\B = Int(Piece(lColor, 3, " ")) * Intensity
					
					Angles = ReadString(f)
					tl\Pitch = Piece(Angles, 1, " ")
					tl\Yaw = Piece(Angles, 2, " ")
					
					tl\InnerConeAngle = ReadInt(f)
					tl\OuterConeAngle = ReadInt(f)
					;[End Block]
				Case "soundemitter"
					;[Block]
					tse.TempSoundEmitters = New TempSoundEmitters
					tse\RoomTemplate = rt
					
					tse\x = ReadFloat(f) * RoomScale
					tse\y = ReadFloat(f) * RoomScale
					tse\z = ReadFloat(f) * RoomScale
					
					tse\ID = ReadInt(f)
					
					tse\Range = ReadFloat(f)
					;[End Block]
				Case "model"
					;[Block]
					tp.TempProps = New TempProps
					tp\RoomTemplate = rt
					
					Temp2s = ReadString(f)
					; ~ A hacky way to use .b3d format
					If FileExtension(Temp2s) = "x" Then Temp2s = Left(Temp2s, Len(Temp2s) - 1) + "b3d"
					tp\Name = "GFX\Map\Props\" + Temp2s
					
					tp\x = ReadFloat(f) * RoomScale
					tp\y = ReadFloat(f) * RoomScale
					tp\z = ReadFloat(f) * RoomScale
					
					tp\Pitch = ReadFloat(f)
					tp\Yaw = ReadFloat(f)
					tp\Roll = ReadFloat(f)
					
					tp\ScaleX = ReadFloat(f)
					tp\ScaleY = ReadFloat(f)
					tp\ScaleZ = ReadFloat(f)
					
					tp\HasCollision = True
					tp\FX = 0
					tp\Texture = ""
					;[End Block]
				Case "mesh"
					;[Block]
					tp.TempProps = New TempProps
					tp\RoomTemplate = rt
					
					tp\x = ReadFloat(f) * RoomScale
					tp\y = ReadFloat(f) * RoomScale
					tp\z = ReadFloat(f) * RoomScale
					
					Temp2s = ReadString(f)
					; ~ A hacky way to use .b3d format
					If FileExtension(Temp2s) = "x"
						Temp2s = Left(Temp2s, Len(Temp2s) - 2)
					ElseIf FileExtension(Temp2s) = "b3d"
						Temp2s = Left(Temp2s, Len(Temp2s) - 4)
					EndIf
					tp\Name = "GFX\Map\Props\" + Temp2s + ".b3d"
					
					tp\Pitch = ReadFloat(f)
					tp\Yaw = ReadFloat(f)
					tp\Roll = ReadFloat(f)
					
					tp\ScaleX = ReadFloat(f)
					tp\ScaleY = ReadFloat(f)
					tp\ScaleZ = ReadFloat(f)
					
					tp\HasCollision = ReadByte(f)
					tp\FX = ReadInt(f)
					tp\Texture = ReadString(f)
					;[End Block]
			End Select
		Next
	EndIf
	
	Local OBJ%
	
	Temp1i = CopyMesh(Alpha)
	FlipMesh(Temp1i)
	AddMesh(Temp1i, Alpha)
	FreeEntity(Temp1i) : Temp1i = 0
	
	If Brush <> 0 Then FreeBrush(Brush) : Brush = 0
	
	AddMesh(Alpha, Opaque)
	FreeEntity(Alpha) : Alpha = 0
	
	EntityFX(Opaque, 3)
	
	EntityAlpha(HiddenMesh, 0.0)
	If HasCollision
		EntityType(HiddenMesh, HIT_MAP)
	Else
		EntityType(HiddenMesh, 0)
	EndIf
	EntityAlpha(Opaque, 1.0)
	
	OBJ = CreatePivot()
	CreatePivot(OBJ) ; ~ Skip "meshes" object
	EntityParent(Opaque, OBJ)
	EntityParent(HiddenMesh, OBJ)
	CreatePivot(OBJ) ; ~ Skip "pointentites" object
	CreatePivot(OBJ) ; ~ Skip "solidentites" object
	EntityParent(CollisionMeshes, OBJ)
	
	CloseFile(f)
	
	CatchErrors("Uncaught: LoadRMesh(" + File + ")")
	
	Return(OBJ)
End Function

Const ForestGridSize% = 10

Type Forest
	Field TileMesh%[5]
	Field DetailMesh%[4]
	Field Grid%[PowTwo(ForestGridSize) + 11]
	Field TileEntities%[PowTwo(ForestGridSize) + 1]
	Field Forest_Pivot%
	Field ForestDoors.Doors[2]
	Field DetailEntities%[2]
End Type

; ~ Forest Constants
;[Block]
Const Deviation_Chance% = 40 ; ~ Out of 100
Const Branch_Chance% = 65
Const Branch_Max_Life% = 4
Const Branch_Die_Chance% = 18
Const Max_Deviation_Distance% = 3
Const Return_Chance% = 27
Const Center% = 5
Const MinDoorPos% = 3, MaxDoorPos% = 7
;[End Block]

Function GenForestGrid%(fr.Forest)
	CatchErrors("GenForestGrid()")
	
	Local Door1Pos%, Door2Pos%
	Local i%, j%
	
	Door1Pos = Rand(MinDoorPos, MaxDoorPos)
	Door2Pos = Rand(MinDoorPos, MaxDoorPos)
	
	; ~ Clear the grid
	For i = 0 To ForestGridSize - 1
		For j = 0 To ForestGridSize - 1
			fr\Grid[(j * ForestGridSize) + i] = 0
		Next
	Next
	
	; ~ Set the position of the concrete and doors
	fr\Grid[Door1Pos] = 3
	fr\Grid[((ForestGridSize - 1) * ForestGridSize) + Door2Pos] = 3
	
	; ~ Generate the path
	Local PathX% = Door2Pos
	Local PathY% = 1
	Local Dir% = 1 ; ~ 0 = left, 1 = up, 2 = right
	
	fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
	
	Local Deviated%
	
	While PathY < ForestGridSize - 4
		If Dir = 1 ; ~ Determine whether to go forward or to the side
			If Chance(Deviation_Chance)
				; ~ Pick a branch direction
				Dir = 2 * Rand(0, 1)
				; ~ Make sure you have not passed max side distance
				Dir = TurnIfDeviating(Max_Deviation_Distance, PathX, Center, Dir)
				Deviated = TurnIfDeviating(Max_Deviation_Distance, PathX, Center, Dir, True)
				If Deviated Then fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
				PathX = MoveForward(Dir, PathX, PathY)
				PathY = MoveForward(Dir, PathX, PathY, True)
			EndIf
		Else
			; ~ We are going to the side, so determine whether to keep going or go forward again
			Dir = TurnIfDeviating(Max_Deviation_Distance, PathX, Center, Dir)
			Deviated = TurnIfDeviating(Max_Deviation_Distance, PathX, Center, Dir, True)
			If Deviated Lor Chance(Return_Chance) Then Dir = 1
			
			PathX = MoveForward(Dir, PathX, PathY)
			PathY = MoveForward(Dir, PathX, PathY, True)
			; ~ If we just started going forward go twice so as to avoid creating a potential 2x2 line
			If Dir = 1
				fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
				PathX = MoveForward(Dir, PathX, PathY)
				PathY = MoveForward(Dir, PathX, PathY, True)
			EndIf
		EndIf
		; ~ Add our position to the grid
		fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
	Wend
	; ~ Finally, bring the path back to the door now that we have reached the end
	Dir = 1
	While PathY < ForestGridSize - 2
		PathX = MoveForward(Dir, PathX, PathY)
		PathY = MoveForward(Dir, PathX, PathY, True)
		fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
	Wend
	
	If PathX <> Door1Pos
		Dir = 0
		If Door1Pos > PathX Then Dir = 2
		While PathX <> Door1Pos
			PathX = MoveForward(Dir, PathX, PathY)
			PathY = MoveForward(Dir, PathX, PathY, True)
			fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
		Wend
	EndIf
	
	; ~ Attempt to create new branches
	Local NewY%, TempY%, NewX%
	Local BranchPos%, LeftMost%, RightMost%
	
	NewY = -3 ; ~ Used for counting off; branches will only be considered once every 4 units so as to avoid potentially too many branches
	While NewY < ForestGridSize - 6
		NewY = NewY + 4
		TempY = NewY
		NewX = 0 
		If Chance(Branch_Chance)
			; ~ Create a branch at this spot
			; ~ Determine if on left or on right
			BranchPos = 2 * Rand(0, 1)
			; ~ Get leftmost or rightmost path in this row
			LeftMost = ForestGridSize - 1
			RightMost = 0
			For i = 0 To ForestGridSize - 1
				If fr\Grid[((ForestGridSize - 1 - NewY) * ForestGridSize) + i] = 1
					If i < LeftMost Then LeftMost = i
					If i > RightMost Then RightMost = i
				EndIf
			Next
			If BranchPos = 0
				NewX = LeftMost - 1
			Else
				NewX = RightMost + 1
			EndIf
			; ~ Before creating a branch make sure it won't pass the border and there are no 1's above or below
			If NewX >= 0 And NewX < ForestGridSize And fr\Grid[((ForestGridSize - 1 - TempY - 1) * ForestGridSize) + NewX] <> 1 And fr\Grid[((ForestGridSize - 1 - TempY + 1) * ForestGridSize) + NewX] <> 1
				fr\Grid[((ForestGridSize - 1 - TempY) * ForestGridSize) + NewX] = -1 ; ~ Make -1s so you don't confuse your branch for a path; will be changed later
				If BranchPos = 0
					NewX = LeftMost - 2
				Else
					NewX = RightMost + 2
				EndIf
				; ~ Before continuing the branch make sure it won't pass the border
				If NewX >= 0 And NewX < ForestGridSize
					fr\Grid[((ForestGridSize - 1 - TempY) * ForestGridSize) + NewX] = -1 ; ~ Branch out twice to avoid creating an unwanted 2x2 path with the real path
					i = 2
					While i < Branch_Max_Life
						i = i + 1
						If Chance(Branch_Die_Chance) Then Exit
						If Rand(0, 3) = 0 ; ~ Have a higher chance to go up to confuse the player
							If BranchPos = 0
								NewX = NewX - 1
							Else
								NewX = NewX + 1
							EndIf
						Else
							TempY = TempY + 1
						EndIf
						
						; ~ before continuing the branch make sure it won't pass the border and there are no 1's above
						If NewX < 0 Lor NewX >= ForestGridSize Lor fr\Grid[((ForestGridSize - 1 - TempY - 1) * ForestGridSize) + NewX] = 1 Then Exit
						
						fr\Grid[((ForestGridSize - 1 - TempY) * ForestGridSize) + NewX] = -1 ; ~ Make -1s so you don't confuse your branch for a path; will be changed later
						If TempY >= ForestGridSize - 2 Then Exit
					Wend
				EndIf
			EndIf
		EndIf
	Wend
	
	If opt\DebugMode
		Local x%, y%
		
		ShowPointer()
		Repeat
			Cls()
			i = ForestGridSize - 1
			For x = 0 To ForestGridSize - 1
				For y = 0 To ForestGridSize - 1
					If fr\Grid[x + (y * ForestGridSize)] = 0
						Color(50, 50, 50)
						Rect((i * 32) * MenuScale, (y * 32) * MenuScale, 30 * MenuScale, 30 * MenuScale)
					Else
						Color(255, 255, 255)
						Rect((i * 32) * MenuScale, (y * 32) * MenuScale, 30 * MenuScale, 30 * MenuScale)
					EndIf
				Next
				i = i - 1
			Next
			
			i = ForestGridSize - 1
			For x = 0 To ForestGridSize - 1
				For y = 0 To ForestGridSize - 1
					If MouseOn((i * 32) * MenuScale, (y * 32) * MenuScale, 32 * MenuScale, 32 * MenuScale)
						Color(255, 0, 0)
					Else
						Color(0, 0, 0)
					EndIf
					TextEx(((i * 32) + 2) * MenuScale, ((y * 32) + 2) * MenuScale, fr\Grid[x + (y * ForestGridSize)])
				Next
				i = i - 1
			Next
			Flip()
			RenderCursor()
		Until GetAnyKey();(GetKey() <> 0 Lor MouseHit(1))
	EndIf
	
	; ~ Change branches from -1s to 1s
	For i = 1 To ForestGridSize - 2
		For j = 0 To ForestGridSize - 1
			If fr\Grid[(i * ForestGridSize) + j] = -1 Then fr\Grid[(i * ForestGridSize) + j] = 1
		Next
	Next
	
	CatchErrors("Uncaught: GenForestGrid()")
End Function

; ~ Shapes ID Constants
;[Block]
Const ROOM1% = 0
Const ROOM2% = 1
Const ROOM2C% = 2
Const ROOM3% = 3
Const ROOM4% = 4
;[End Block]

Function PlaceForest%(fr.Forest, x#, y#, z#, r.Rooms)
	CatchErrors("PlaceForest(" + x + ", " + y + ", " + z + ")")
	
	Local tX%, tY%
	Local Tile_Size# = 12.0
	Local Tile_Type%
	Local Tile_Entity%, Detail_Entity%
	Local Tempf1#, Tempf2#, Tempf3#, Tempf4#
	Local i%, Width%, lX%, lY%, d%
	
	DestroyForest(fr, False)
	
	fr\Forest_Pivot = CreatePivot()
	PositionEntity(fr\Forest_Pivot, x, y, z, True)
	
	; ~ Load assets
	Local hMap%[5], Mask%[5]
	Local GroundTexture% = LoadTexture_Strict("GFX\Map\Textures\forestfloor.jpg")
	Local PathTexture% = LoadTexture_Strict("GFX\Map\Textures\forestpath.jpg")
	
	If opt\Atmosphere
		TextureBlend(GroundTexture, 5)
		TextureBlend(PathTexture, 5)
	EndIf
	
	hMap[ROOM1] = LoadImage_Strict("GFX\Map\Rooms\Forest\forest1h.png")
	Mask[ROOM1] = LoadTexture_Strict("GFX\Map\Rooms\Forest\forest1h_mask.png", 1 + 2)
	
	hMap[ROOM2] = LoadImage_Strict("GFX\Map\Rooms\Forest\forest2h.png")
	Mask[ROOM2] = LoadTexture_Strict("GFX\Map\Rooms\Forest\forest2h_mask.png", 1 + 2)
	
	hMap[ROOM2C] = LoadImage_Strict("GFX\Map\Rooms\Forest\forest2Ch.png")
	Mask[ROOM2C] = LoadTexture_Strict("GFX\Map\Rooms\Forest\forest2Ch_mask.png", 1 + 2)
	
	hMap[ROOM3] = LoadImage_Strict("GFX\Map\Rooms\Forest\forest3h.png")
	Mask[ROOM3] = LoadTexture_Strict("GFX\Map\Rooms\Forest\forest3h_mask.png", 1 + 2)
	
	hMap[ROOM4] = LoadImage_Strict("GFX\Map\Rooms\Forest\forest4h.png")
	Mask[ROOM4] = LoadTexture_Strict("GFX\Map\Rooms\Forest\forest4h_mask.png", 1 + 2)
	
	For i = ROOM1 To ROOM4
		fr\TileMesh[i] = LoadTerrain(hMap[i], 0.03, GroundTexture, PathTexture, Mask[i])
		HideEntity(fr\TileMesh[i])
		DeleteSingleTextureEntryFromCache(Mask[i])
	Next
	DeleteSingleTextureEntryFromCache(GroundTexture)
	DeleteSingleTextureEntryFromCache(PathTexture)
	
	; ~ Detail meshes
	fr\DetailMesh[0] = LoadMesh_Strict("GFX\Map\Props\tree1.b3d")
	fr\DetailMesh[1] = LoadMesh_Strict("GFX\Map\Props\rock.b3d")
	fr\DetailMesh[2] = LoadMesh_Strict("GFX\Map\Props\tree2.b3d")
	fr\DetailMesh[3] = LoadRMesh("GFX\Map\Rooms\scp_860_1_wall.rmesh", Null, False)
	
	For i = 0 To 3
		HideEntity(fr\DetailMesh[i])
	Next
	
	Tempf3 = MeshWidth(fr\TileMesh[ROOM1])
	Tempf1 = Tile_Size / Tempf3
	
	For tX = 0 To ForestGridSize - 1
		For tY = 1 To ForestGridSize - 2
			If fr\Grid[(tY * ForestGridSize) + tX] = 1
				Tile_Type = 0
				If tX + 1 < ForestGridSize Then Tile_Type = (fr\Grid[(tY * ForestGridSize) + tX + 1] > 0)
				If tX - 1 >= 0 Then Tile_Type = Tile_Type + (fr\Grid[(tY * ForestGridSize) + tX - 1] > 0)
				
				If tY + 1 < ForestGridSize Then Tile_Type = Tile_Type + (fr\Grid[((tY + 1) * ForestGridSize) + tX] > 0)
				If tY - 1 >= 0 Then Tile_Type = Tile_Type + (fr\Grid[((tY - 1) * ForestGridSize) + tX] > 0)
				
				Local Angle# = 0.0
				
				Select(Tile_Type)
					Case 1
						;[Block]
						Tile_Entity = CopyEntity(fr\TileMesh[ROOM1])
						
						If fr\Grid[((tY + 1) * ForestGridSize) + tX] > 0
							Angle = 180.0
						ElseIf fr\Grid[(tY * ForestGridSize) + (tX - 1)] > 0
							Angle = 270.0
						ElseIf fr\Grid[(tY * ForestGridSize) + (tX + 1)] > 0
							Angle = 90.0
						Else
							Angle = 0.0
						EndIf
						
						Tile_Type = ROOM1 + 1
						;[End Block]
					Case 2
						;[Block]
						If fr\Grid[((tY - 1) * ForestGridSize) + tX] > 0 And fr\Grid[((tY + 1) * ForestGridSize) + tX] > 0
							Tile_Entity = CopyEntity(fr\TileMesh[ROOM2])
							Tile_Type = ROOM2 + 1
						ElseIf fr\Grid[(tY * ForestGridSize) + tX + 1] > 0 And fr\Grid[(tY * ForestGridSize) + tX - 1] > 0
							Tile_Entity = CopyEntity(fr\TileMesh[ROOM2])
							Angle = 90.0
							Tile_Type = ROOM2 + 1
						Else
							Tile_Entity = CopyEntity(fr\TileMesh[ROOM2C])
							If fr\Grid[(tY * ForestGridSize) + tX - 1] > 0 And fr\Grid[((tY + 1) * ForestGridSize) + tX] > 0
								Angle = 180.0
							ElseIf fr\Grid[(tY * ForestGridSize) + tX + 1] > 0 And fr\Grid[((tY - 1) * ForestGridSize) + tX] > 0
								Angle = 0.0
							ElseIf fr\Grid[(tY * ForestGridSize) + tX - 1] > 0 And fr\Grid[((tY - 1) * ForestGridSize) + tX] > 0
								Angle = 270.0
							Else
								Angle = 90.0
							EndIf
							Tile_Type = ROOM2C + 1
						EndIf
						;[End Block]
					Case 3
						;[Block]
						Tile_Entity = CopyEntity(fr\TileMesh[ROOM3])
						
						If fr\Grid[((tY - 1) * ForestGridSize) + tX] = 0
							Angle = 180.0
						ElseIf fr\Grid[(tY * ForestGridSize) + tX - 1] = 0
							Angle = 90.0
						ElseIf fr\Grid[(tY * ForestGridSize) + tX + 1] = 0
							Angle = 270.0
						Else
							Angle = 0.0
						EndIf
						
						Tile_Type = ROOM3 + 1
						;[End Block]
					Case 4
						;[Block]
						Tile_Entity = CopyEntity(fr\TileMesh[ROOM4])
						
						Angle = (fr\Grid[(tY * ForestGridSize) + tX] Mod 4) * 90.0
						
						Tile_Type = ROOM4 + 1
						;[End Block]
				End Select
				
				If Tile_Type > 0
					; ~ Place trees and other details
					; ~ Only placed on spots where the value of the heightmap is above 100
					SetBuffer(ImageBuffer(hMap[Tile_Type - 1]))
					Width = ImageWidth(hMap[Tile_Type - 1])
					Tempf4 = (Tempf3 / Float(Width))
					For lX = 3 To Width - 2
						For lY = 3 To Width - 2
							GetColor(lX, Width - lY)
							
							Local ColorR% = ColorRed()
							Local DetailEntityPosX# = (lX * Tempf4) - (Tempf3 / 2.0)
							Local DetailEntityPosZ# = (lY * Tempf4) - (Tempf3 / 2.0)
							
							If ColorR > Rand(100, 260)
								Detail_Entity = 0
								Select(Rand(0, 7))
									Case 0, 1, 2, 3, 4, 5, 6 ; ~ Create a tree
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[0])
										Tempf2 = Rnd(0.25, 0.4)
										For i = 0 To 3
											d = CopyEntity(fr\DetailMesh[2])
											RotateEntity(d, 0.0, (90.0 * i) + Rnd(-20.0, 20.0), 0.0)
											EntityParent(d, Detail_Entity)
											EntityFX(d, 1)
										Next
										ScaleEntity(Detail_Entity, Tempf2 * 1.1, Tempf2, Tempf2 * 1.1, True)
										PositionEntity(Detail_Entity, DetailEntityPosX, ColorR * 0.03 - Rnd(3.0, 3.2), DetailEntityPosZ, True)
										RotateEntity(Detail_Entity, Rnd(-5.0, 5.0), Rnd(360.0), 0.0, True)
										;[End Block]
									Case 6 ; ~ Add a stump
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[2])
										Tempf2 = Rnd(0.1, 0.12)
										ScaleEntity(Detail_Entity, Tempf2, Tempf2, Tempf2, True)
										PositionEntity(Detail_Entity, DetailEntityPosX, ColorR * 0.03 - 1.3, DetailEntityPosZ, True)
										;[End Block]
									Case 7 ; ~ Add a rock
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[1])
										Tempf2 = Rnd(0.01, 0.012)
										PositionEntity(Detail_Entity, DetailEntityPosX, ColorR * 0.03 - 1.3, DetailEntityPosZ, True)
										RotateEntity(Detail_Entity, 0.0, Rnd(360.0), 0.0, True)
										;[End Block]
								End Select
								If Detail_Entity <> 0
									EntityFX(Detail_Entity, 1)
									EntityParent(Detail_Entity, Tile_Entity)
								EndIf
							EndIf
						Next
					Next
					SetBuffer(BackBuffer())
					
					ScaleEntity(Tile_Entity, Tempf1, Tempf1, Tempf1)
					
					Local ItemPlaced%[4], iX#, iZ#
					Local tYFloor% = Floor(tY / 3)
					Local it.Items = Null
					
					If (tY Mod 3) = 2 And (Not ItemPlaced[tYFloor])
						ItemPlaced[tYFloor] = True
						
						If Tile_Type = ROOM1 + 1
							iX = 0.4 : iZ = 0.0
						ElseIf Tile_Type = ROOM2C + 1
							iX = 1.7 : iZ = -1.0
						Else
							iX = 0.0 : iZ = 0.0
						EndIf
;						it.Items = CreateItem("Log #" + Int(tYFloor + 1), "paper", iX, 0.2, iZ)
;						EntityType(it\Collider, HIT_ITEM)
;						EntityParent(it\Collider, Tile_Entity)
					EndIf
					
					TurnEntity(Tile_Entity, 0.0, Angle, 0.0)
					PositionEntity(Tile_Entity, x + (tX * Tile_Size), y, z + (tY * Tile_Size), True)
					EntityType(Tile_Entity, HIT_MAP)
					EntityFX(Tile_Entity, 1)
					EntityParent(Tile_Entity, fr\Forest_Pivot)
					EntityPickMode(Tile_Entity, 2)
					
					If it <> Null Then EntityParent(it\Collider, 0)
					
					fr\TileEntities[tX + (tY * ForestGridSize)] = Tile_Entity
					HideEntity(fr\TileEntities[tX + (tY * ForestGridSize)])
				EndIf
			EndIf
		Next
	Next
	FreeImage(hMap[i]) : hMap[i] = 0
	
	; ~ Place the wall
	For i = 0 To 1
		tY = i * (ForestGridSize - 1)
		For tX = MinDoorPos To MaxDoorPos
			If fr\Grid[(tY * ForestGridSize) + tX] = 3
				fr\DetailEntities[i] = CopyEntity(fr\DetailMesh[3])
				ScaleEntity(fr\DetailEntities[i], RoomScale, RoomScale, RoomScale)
				
				fr\ForestDoors[i] = CreateDoor(Null, 0.0, 32.0 * RoomScale, 0.0, 180.0, False, WOODEN_DOOR, KEY_860, "", fr\DetailEntities[i])
				fr\ForestDoors[i]\Locked = 2
				
				EntityType(fr\DetailEntities[i], HIT_MAP)
				EntityPickMode(fr\DetailEntities[i], 2)
				PositionEntity(fr\DetailEntities[i], x + (tX * Tile_Size), y, z + (tY * Tile_Size) + (Tile_Size / 2) - (Tile_Size * i), True)
				RotateEntity(fr\DetailEntities[i], 0.0, 180.0 * i, 0.0)
				EntityParent(fr\DetailEntities[i], fr\Forest_Pivot)
				Exit
			EndIf
		Next
	Next
	
	CatchErrors("Uncaught: PlaceForest(" + x + ", " + y + ", " + z + ")")
End Function

Function PlaceMapCreatorForest%(fr.Forest, x#, y#, z#, r.Rooms)
	CatchErrors("PlaceMapCreatorForest(" + x + ", " + y + ", " + z + ")")
	
	Local tX%, tY%
	Local Tile_Size# = 12.0
	Local Tile_Type%, Detail_Entity%
	Local Tile_Entity%, Eetail_Entity%
	Local Tempf1#, Tempf2#, Tempf3#, Tempf4#
	Local i%, Width%, lX%, lY%, d%
	
	DestroyForest(fr, False)
	
	fr\Forest_Pivot = CreatePivot()
	PositionEntity(fr\Forest_Pivot, x, y, z, True)
	
	Local hMap%[5], Mask%[5]
	; ~ Load assets
	Local GroundTexture% = LoadTexture_Strict("GFX\Map\Textures\forestfloor.jpg", 1 + 256)
	Local PathTexture% = LoadTexture_Strict("GFX\Map\Textures\forestpath.jpg", 1 + 256)
	
	If opt\Atmosphere
		TextureBlend(GroundTexture, 5)
		TextureBlend(PathTexture, 5)
	EndIf
	
	hMap[ROOM1] = LoadImage_Strict("GFX\Map\Rooms\Forest\forest1h.png")
	Mask[ROOM1] = LoadTexture_Strict("GFX\Map\Rooms\Forest\forest1h_mask.png", 1 + 2 + 256)
	
	hMap[ROOM2] = LoadImage_Strict("GFX\Map\Rooms\Forest\forest2h.png")
	Mask[ROOM2] = LoadTexture_Strict("GFX\Map\Rooms\Forest\forest2h_mask.png", 1 + 2 + 256)
	
	hMap[ROOM2C] = LoadImage_Strict("GFX\Map\Rooms\Forest\forest2Ch.png")
	Mask[ROOM2C] = LoadTexture_Strict("GFX\Map\Rooms\Forest\forest2Ch_mask.png", 1 + 2 + 256)
	
	hMap[ROOM3] = LoadImage_Strict("GFX\Map\Rooms\Forest\forest3h.png")
	Mask[ROOM3] = LoadTexture_Strict("GFX\Map\Rooms\Forest\forest3h_mask.png", 1 + 2 + 256)
	
	hMap[ROOM4] = LoadImage_Strict("GFX\Map\Rooms\Forest\forest4h.png")
	Mask[ROOM4] = LoadTexture_Strict("GFX\Map\Rooms\Forest\forest4h_mask.png", 1 + 2 + 256)
	
	For i = ROOM1 To ROOM4
		fr\TileMesh[i] = LoadTerrain(hMap[i], 0.03, GroundTexture, PathTexture, Mask[i])
		HideEntity(fr\TileMesh[i])
		DeleteSingleTextureEntryFromCache(Mask[i])
	Next
	DeleteSingleTextureEntryFromCache(GroundTexture)
	DeleteSingleTextureEntryFromCache(PathTexture)
	
	; ~ Detail meshes
	fr\DetailMesh[0] = LoadMesh_Strict("GFX\Map\Props\tree1.b3d")
	fr\DetailMesh[1] = LoadMesh_Strict("GFX\Map\Props\rock.b3d")
	fr\DetailMesh[2] = LoadMesh_Strict("GFX\Map\Props\tree2.b3d")
	fr\DetailMesh[3] = LoadRMesh("GFX\Map\Rooms\scp_860_1_wall.rmesh", Null, False)
	
	For i = 0 To 3
		HideEntity(fr\DetailMesh[i])
	Next
	
	Tempf3 = MeshWidth(fr\TileMesh[ROOM1])
	Tempf1 = Tile_Size / Tempf3
	
	For tX = 0 To ForestGridSize - 1
		For tY = 0 To ForestGridSize - 1
			If fr\Grid[(tY * ForestGridSize) + tX] > 0
				Tile_Type = 0
				
				Local Angle# = 0.0
				
				Tile_Type = Ceil(Float(fr\Grid[(tY * ForestGridSize) + tX]) / 4.0)
				If Tile_Type = 6 Then Tile_Type = 2
				Angle = (fr\Grid[(tY * ForestGridSize) + tX] Mod 4) * 90.0
				
				Tile_Entity = CopyEntity(fr\TileMesh[Tile_Type - 1])
				
				If Tile_Type > 0
					; ~ Place trees and other details
					; ~ Only placed on spots where the value of the heightmap is above 100
					SetBuffer(ImageBuffer(hMap[Tile_Type - 1]))
					Width = ImageWidth(hMap[Tile_Type - 1])
					Tempf4 = (Tempf3 / Float(Width))
					For lX = 3 To Width - 2
						For lY = 3 To Width - 2
							GetColor(lX, Width - lY)
							
							Local ColorR% = ColorRed()
							Local DetailEntityPosX# = (lX * Tempf4) - (Tempf3 / 2.0)
							Local DetailEntityPosZ# = (lY * Tempf4) - (Tempf3 / 2.0)
							
							If ColorR > Rand(100, 260)
								Detail_Entity = 0
								Select(Rand(0, 7))
									Case 0, 1, 2, 3, 4, 5, 6 ; ~ Create a tree
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[0])
										Tempf2 = Rnd(0.25, 0.4)
										For i = 0 To 3
											d = CopyEntity(fr\DetailMesh[2])
											RotateEntity(d, 0.0, (90.0 * i) + Rnd(-20.0, 20.0), 0.0)
											EntityParent(d, Detail_Entity)
											EntityFX(d, 1)
										Next
										ScaleEntity(Detail_Entity, Tempf2 * 1.1, Tempf2, Tempf2 * 1.1, True)
										PositionEntity(Detail_Entity, DetailEntityPosX, ColorR * 0.03 - Rnd(3.0, 3.2), DetailEntityPosZ, True)
										RotateEntity(Detail_Entity, Rnd(-5.0, 5.0), Rnd(360.0), 0.0, True)
										;[End Block]
									Case 6 ; ~ Add a stump
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[2])
										Tempf2 = Rnd(0.1, 0.12)
										ScaleEntity(Detail_Entity, Tempf2, Tempf2, Tempf2, True)
										PositionEntity(Detail_Entity, DetailEntityPosX, ColorR * 0.03 - 1.3, DetailEntityPosZ, True)
										;[End Block]
									Case 7 ; ~ Add a rock
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[1])
										Tempf2 = Rnd(0.01, 0.012)
										PositionEntity(Detail_Entity, DetailEntityPosX, ColorR * 0.03 - 1.3, DetailEntityPosZ, True)
										RotateEntity(Detail_Entity, 0.0, Rnd(360.0), 0.0, True)
										;[End Block]
								End Select
								If Detail_Entity <> 0
									EntityFX(Detail_Entity, 1)
									EntityParent(Detail_Entity, Tile_Entity)
								EndIf
							EndIf
						Next
					Next
					SetBuffer(BackBuffer())
					
					ScaleEntity(Tile_Entity, Tempf1, Tempf1, Tempf1)
					
					Local ItemPlaced%[4], iX#, iZ#
					Local tYFloor% = Floor(tY / 3)
					Local it.Items = Null
					
					If (tY Mod 3) = 2 And (Not ItemPlaced[tYFloor])
						ItemPlaced[tYFloor] = True
						
						If Tile_Type = ROOM1 + 1
							iX = 0.4 : iZ = 0.0
						ElseIf Tile_Type = ROOM2C + 1
							iX = 1.7 : iZ = -1.0
						Else
							iX = 0.0 : iZ = 0.0
						EndIf
;						it.Items = CreateItem("Log #" + Int(tYFloor + 1), "paper", iX, 0.2, iZ)
;						EntityType(it\Collider, HIT_ITEM)
;						EntityParent(it\Collider, Tile_Entity)
					EndIf
					
					TurnEntity(Tile_Entity, 0.0, Angle, 0.0)
					PositionEntity(Tile_Entity, x + (tX * Tile_Size), y, z + (tY * Tile_Size), True)
					EntityType(Tile_Entity, HIT_MAP)
					EntityFX(Tile_Entity, 1)
					EntityParent(Tile_Entity, fr\Forest_Pivot)
					EntityPickMode(Tile_Entity, 2)
					
					If it <> Null Then EntityParent(it\Collider, 0)
					
					fr\TileEntities[tX + (tY * ForestGridSize)] = Tile_Entity
					HideEntity(fr\TileEntities[tX + (tY * ForestGridSize)])
				EndIf
				
				If Ceil(Float(fr\Grid[(tY * ForestGridSize) + tX]) / 4.0) = 6
					For i = 0 To 1
						If fr\ForestDoors[i] = Null
							fr\DetailEntities[i] = CopyEntity(fr\DetailMesh[3])
							ScaleEntity(fr\DetailEntities[i], RoomScale, RoomScale, RoomScale)
							
							fr\ForestDoors[i] = CreateDoor(Null, 0.0, 32.0 * RoomScale, 0.0, 180.0, False, WOODEN_DOOR, KEY_860, "", fr\DetailEntities[i])
							fr\ForestDoors[i]\Locked = 2
							
							EntityType(fr\DetailEntities[i], HIT_MAP)
							EntityPickMode(fr\DetailEntities[i], 2)
							PositionEntity(fr\DetailEntities[i], x + (tX * Tile_Size), y, z + (tY * Tile_Size), True)
							RotateEntity(fr\DetailEntities[i], 0.0, Angle + 180.0, 0.0)
							MoveEntity(fr\DetailEntities[i], 0.0, 0.0, -6.0)
							EntityParent(fr\DetailEntities[i], fr\Forest_Pivot)
							Exit
						EndIf
					Next
				EndIf
			EndIf
		Next
	Next
	FreeImage(hMap[i]) : hMap[i] = 0
	
	CatchErrors("Uncaught: PlaceMapCreatorForest(" + x + ", " + y + ", " + z + ")")
End Function

Function DestroyForest%(fr.Forest, RemoveGrid% = True)
	CatchErrors("DestroyForest(" + RemoveGrid + ")")
	
	Local tX%, tY%, i%
	
	For tX = 0 To ForestGridSize - 1
		For tY = 0 To ForestGridSize - 1
			If fr\TileEntities[tX + (tY * ForestGridSize)] <> 0
				FreeEntity(fr\TileEntities[tX + (tY * ForestGridSize)]) : fr\TileEntities[tX + (tY * ForestGridSize)] = 0
				If RemoveGrid Then fr\Grid[tX + (tY * ForestGridSize)] = 0
			EndIf
		Next
	Next
	For i = 0 To 1
		If fr\ForestDoors[i] <> Null Then RemoveDoor(fr\ForestDoors[i])
		If fr\DetailEntities[i] <> 0 Then FreeEntity(fr\DetailEntities[i]) : fr\DetailEntities[i] = 0
	Next
	If fr\Forest_Pivot <> 0 Then FreeEntity(fr\Forest_Pivot) : fr\Forest_Pivot = 0
	For i = ROOM1 To ROOM4
		If fr\TileMesh[i] <> 0 Then FreeEntity(fr\TileMesh[i]) : fr\TileMesh[i] = 0
	Next
	For i = 0 To 2 Step 2
		If fr\DetailMesh[i] <> 0 Then FreeEntity(fr\DetailMesh[i]) : fr\DetailMesh[i] = 0
		If fr\DetailMesh[i + 1] <> 0 Then FreeEntity(fr\DetailMesh[i + 1]) : fr\DetailMesh[i + 1] = 0
	Next
	
	CatchErrors("Uncaught: DestroyForest(" + RemoveGrid + ")")
End Function

Function UpdateForest%(fr.Forest)
	CatchErrors("UpdateForest()")
	
	Local tX%, tY%
	Local Dist#
	
	For tX = 0 To ForestGridSize - 1
		For tY = 0 To ForestGridSize - 1
			If fr\TileEntities[tX + (tY * ForestGridSize)] <> 0
				Dist = Sqr(PowTwo(EntityX(me\Collider, True) - EntityX(fr\TileEntities[tX + (tY * ForestGridSize)], True)) + PowTwo(EntityZ(me\Collider, True) - EntityZ(fr\TileEntities[tX + (tY * ForestGridSize)], True)))
				
				If Dist < HideDistance
					If EntityHidden(fr\TileEntities[tX + (tY * ForestGridSize)]) Then ShowEntity(fr\TileEntities[tX + (tY * ForestGridSize)])
				Else
					If (Not EntityHidden(fr\TileEntities[tX + (tY * ForestGridSize)])) Then HideEntity(fr\TileEntities[tX + (tY * ForestGridSize)])
				EndIf
			EndIf
		Next
	Next
	
	CatchErrors("Uncaught: UpdateForest()")
End Function

Global RoomTempID%
Global RoomAmbience%[10]

Type RoomTemplates
	Field OBJ%, ID%
	Field OBJPath$
	Field Zone%[5]
	Field Shape%, RoomID%
	Field Commonness%, Large%
	Field DisableDecals%
	;Field TempTriggerBoxAmount%
	;Field TempTriggerBox%[8]
	;Field TempTriggerBoxName$[8]
	Field DisableOverlapCheck% = True
	Field MinX#, MinY#, MinZ#
	Field MaxX#, MaxY#, MaxZ#
End Type

; ~ Room ID constants
;[Block]
Const r_room1_archive% = 0
Const r_room1_dead_end_lcz% = 1
Const r_cont1_005% = 2
Const r_cont1_173% = 3, r_cont1_173_intro% = 4, r_cont1_205% = 5, r_cont1_372% = 6, r_cont1_914% = 7
Const r_room2_lcz% = 8, r_room2_2_lcz% = 9, r_room2_3_lcz% = 10, r_room2_4_lcz% = 11, r_room2_5_lcz% = 12, r_room2_6_lcz% = 13
Const r_room2_closets% = 14
Const r_room2_elevator% = 15
Const r_room2_gw% = 16, r_room2_gw_2% = 17
Const r_room2_js% = 18
Const r_room2_sl% = 19
Const r_room2_storage% = 20
Const r_room2_tesla_lcz% = 21
Const r_room2_test_lcz% = 22
Const r_cont2_012% = 23, r_cont2_427_714_860_1025% = 24, r_cont2_500_1499% = 25, r_cont2_1123% = 26
Const r_room2c_lcz% = 27, r_room2c_2_lcz% = 28
Const r_room2c_gw_lcz% = 29, r_room2c_gw_2_lcz% = 30
Const r_cont2c_066_1162_arc% = 32
Const r_room3_storage% = 33
Const r_room3_lcz% = 34, r_room3_2_lcz% = 35, r_room3_3_lcz% = 36
Const r_room4_lcz% = 37, r_room4_2_lcz% = 38
Const r_room4_ic% = 39
Const r_room2_checkpoint_lcz_hcz% = 40
Const r_room1_dead_end_hcz% = 41
Const r_cont1_035% = 42, r_cont1_079% = 43, r_cont1_096% = 44, r_cont1_106% = 45, r_cont1_895% = 46
Const r_room2_hcz% = 47, r_room2_2_hcz% = 48, r_room2_3_hcz% = 49, r_room2_4_hcz% = 50, r_room2_5_hcz% = 51, r_room2_6_hcz% = 52
Const r_room2_mt% = 53
Const r_room2_nuke% = 54
Const r_room2_servers_hcz% = 55
Const r_room2_shaft% = 56
Const r_room2_tesla_hcz% = 57
Const r_room2_test_hcz% = 58
Const r_cont2_008% = 59, r_cont2_049% = 60, r_cont2_409% = 61
Const r_room2c_hcz% = 62
Const r_room2c_maintenance% = 63
Const r_room3_hcz% = 64, r_room3_2_hcz% = 65, r_room3_3_hcz% = 66
Const r_cont3_513% = 67, r_cont3_966% = 68
Const r_room4_hcz% = 69, r_room4_2_hcz% = 70
Const r_room2_checkpoint_hcz_ez% = 71
Const r_gate_a_entrance% = 72, r_gate_a% = 73, r_gate_b_entrance% = 74, r_gate_b% = 75
Const r_room1_dead_end_ez% = 76
Const r_room1_lifts% = 77
Const r_room1_o5% = 78
Const r_room2_ez% = 79, r_room2_2_ez% = 80, r_room2_3_ez% = 81, r_room2_4_ez% = 82, r_room2_5_ez% = 83, r_room2_6_ez% = 84
Const r_room2_bio% = 85
Const r_room2_cafeteria% = 86
Const r_room2_ic% = 87
Const r_room2_medibay% = 88
Const r_room2_office% = 89, r_room2_office_2% = 90, r_room2_office_3% = 91
Const r_room2_servers_ez% = 92
Const r_room2_scientists% = 93, r_room2_scientists_2% = 94
Const r_room2_tesla_ez% = 95
Const r_cont2_860_1% = 96
Const r_room2c_ez% = 97, r_room2c_2_ez% = 98
Const r_room2c_ec% = 99
Const r_room2c_gw_ez% = 100
Const r_room3_gw% = 101
Const r_room3_office% = 102
Const r_room3_ez% = 103, r_room3_2_ez% = 104, r_room3_3_ez% = 105, r_room3_4_ez% = 106
Const r_room4_ez% = 107
Const r_dimension_106% = 108, r_dimension_1499% = 109
;[End Block]

Function FindRoomID%(RoomName$)
	Select(RoomName)
		Case "room1_archive"
			;[Block]
			Return(r_room1_archive)
			;[End Block]
		Case "room1_dead_end_lcz"
			;[Block]
			Return(r_room1_dead_end_lcz)
			;[End Block]
		Case "cont1_005"
			;[Block]
			Return(r_cont1_005)
			;[End Block]
		Case "cont1_173"
			;[Block]
			Return(r_cont1_173)
			;[End Block]
		Case "cont1_173_intro"
			;[Block]
			Return(r_cont1_173_intro)
			;[End Block]
		Case "cont1_205"
			;[Block]
			Return(r_cont1_205)
			;[End Block]
		Case "cont1_372"
			;[Block]
			Return(r_cont1_372)
			;[End Block]
		Case "cont1_914"
			;[Block]
			Return(r_cont1_914)
			;[End Block]
		Case "room2_lcz"
			;[Block]
			Return(r_room2_lcz)
			;[End Block]
		Case "room2_2_lcz"
			;[Block]
			Return(r_room2_2_lcz)
			;[End Block]
		Case "room2_3_lcz"
			;[Block]
			Return(r_room2_3_lcz)
			;[End Block]
		Case "room2_4_lcz"
			;[Block]
			Return(r_room2_4_lcz)
			;[End Block]
		Case "room2_5_lcz"
			;[Block]
			Return(r_room2_5_lcz)
			;[End Block]
		Case "room2_6_lcz"
			;[Block]
			Return(r_room2_6_lcz)
			;[End Block]
		Case "room2_closets"
			;[Block]
			Return(r_room2_closets)
			;[End Block]
		Case "room2_elevator"
			;[Block]
			Return(r_room2_elevator)
			;[End Block]
		Case "room2_gw"
			;[Block]
			Return(r_room2_gw)
			;[End Block]
		Case "room2_gw_2"
			;[Block]
			Return(r_room2_gw_2)
			;[End Block]
		Case "room2_js"
			;[Block]
			Return(r_room2_js)
			;[End Block]
		Case "room2_sl"
			;[Block]
			Return(r_room2_sl)
			;[End Block]
		Case "room2_storage"
			;[Block]
			Return(r_room2_storage)
			;[End Block]
		Case "room2_tesla_lcz"
			;[Block]
			Return(r_room2_tesla_lcz)
			;[End Block]
		Case "room2_test_lcz"
			;[Block]
			Return(r_room2_test_lcz)
			;[End Block]
		Case "cont2_012"
			;[Block]
			Return(r_cont2_012)
			;[End Block]
		Case "cont2_427_714_860_1025"
			;[Block]
			Return(r_cont2_427_714_860_1025)
			;[End Block]
		Case "cont2_500_1499"
			;[Block]
			Return(r_cont2_500_1499)
			;[End Block]
		Case "cont2_1123"
			;[Block]
			Return(r_cont2_1123)
			;[End Block]
		Case "room2c_lcz"
			;[Block]
			Return(r_room2c_lcz)
			;[End Block]
		Case "room2c_2_lcz"
			;[Block]
			Return(r_room2c_2_lcz)
			;[End Block]
		Case "room2c_gw_lcz"
			;[Block]
			Return(r_room2c_gw_lcz)
			;[End Block]
		Case "room2c_gw_2_lcz"
			;[Block]
			Return(r_room2c_gw_2_lcz)
			;[End Block]
		Case "cont2c_066_1162_arc"
			;[Block]
			Return(r_cont2c_066_1162_arc)
			;[End Block]
		Case "room3_storage"
			;[Block]
			Return(r_room3_storage)
			;[End Block]
		Case "room3_lcz"
			;[Block]
			Return(r_room3_lcz)
			;[End Block]
		Case "room3_2_lcz"
			;[Block]
			Return(r_room3_2_lcz)
			;[End Block]
		Case "room3_3_lcz"
			;[Block]
			Return(r_room3_3_lcz)
			;[End Block]
		Case "room4_lcz"
			;[Block]
			Return(r_room4_lcz)
			;[End Block]
		Case "room4_2_lcz"
			;[Block]
			Return(r_room4_2_lcz)
			;[End Block]
		Case "room4_ic"
			;[Block]
			Return(r_room4_ic)
			;[End Block]
		Case "room2_checkpoint_lcz_hcz"
			;[Block]
			Return(r_room2_checkpoint_lcz_hcz)
			;[End Block]
		Case "room1_dead_end_hcz"
			;[Block]
			Return(r_room1_dead_end_hcz)
			;[End Block]
		Case "cont1_035"
			;[Block]
			Return(r_cont1_035)
			;[End Block]
		Case "cont1_079"
			;[Block]
			Return(r_cont1_079)
			;[End Block]
		Case "cont1_096"
			;[Block]
			Return(r_cont1_096)
			;[End Block]
		Case "cont1_106"
			;[Block]
			Return(r_cont1_106)
			;[End Block]
		Case "cont1_895"
			;[Block]
			Return(r_cont1_895)
			;[End Block]
		Case "room2_hcz"
			;[Block]
			Return(r_room2_hcz)
			;[End Block]
		Case "room2_2_hcz"
			;[Block]
			Return(r_room2_2_hcz)
			;[End Block]
		Case "room2_3_hcz"
			;[Block]
			Return(r_room2_3_hcz)
			;[End Block]
		Case "room2_4_hcz"
			;[Block]
			Return(r_room2_4_hcz)
			;[End Block]
		Case "room2_5_hcz"
			;[Block]
			Return(r_room2_5_hcz)
			;[End Block]
		Case "room2_6_hcz"
			;[Block]
			Return(r_room2_6_hcz)
			;[End Block]
		Case "room2_mt"
			;[Block]
			Return(r_room2_mt)
			;[End Block]
		Case "room2_nuke"
			;[Block]
			Return(r_room2_nuke)
			;[End Block]
		Case "room2_servers_hcz"
			;[Block]
			Return(r_room2_servers_hcz)
			;[End Block]
		Case "room2_shaft"
			;[Block]
			Return(r_room2_shaft)
			;[End Block]
		Case "room2_tesla_hcz"
			;[Block]
			Return(r_room2_tesla_hcz)
			;[End Block]
		Case "room2_test_hcz"
			;[Block]
			Return(r_room2_test_hcz)
			;[End Block]
		Case "cont2_008"
			;[Block]
			Return(r_cont2_008)
			;[End Block]
		Case "cont2_049"
			;[Block]
			Return(r_cont2_049)
			;[End Block]
		Case "cont2_409"
			;[Block]
			Return(r_cont2_409)
			;[End Block]
		Case "room2c_hcz"
			;[Block]
			Return(r_room2c_hcz)
			;[End Block]
		Case "room2c_maintenance"
			;[Block]
			Return(r_room2c_maintenance)
			;[End Block]
		Case "room3_hcz"
			;[Block]
			Return(r_room3_hcz)
			;[End Block]
		Case "room3_2_hcz"
			;[Block]
			Return(r_room3_2_hcz)
			;[End Block]
		Case "room3_3_hcz"
			;[Block]
			Return(r_room3_3_hcz)
			;[End Block]
		Case "cont3_513"
			;[Block]
			Return(r_cont3_513)
			;[End Block]
		Case "cont3_966"
			;[Block]
			Return(r_cont3_966)
			;[End Block]
		Case "room4_hcz"
			;[Block]
			Return(r_room4_hcz)
			;[End Block]
		Case "room4_2_hcz"
			;[Block]
			Return(r_room4_2_hcz)
			;[End Block]
		Case "room2_checkpoint_hcz_ez"
			;[Block]
			Return(r_room2_checkpoint_hcz_ez)
			;[End Block]
		Case "gate_a_entrance"
			;[Block]
			Return(r_gate_a_entrance)
			;[End Block]
		Case "gate_a"
			;[Block]
			Return(r_gate_a)
			;[End Block]
		Case "gate_b_entrance"
			;[Block]
			Return(r_gate_b_entrance)
			;[End Block]
		Case "gate_b"
			;[Block]
			Return(r_gate_b)
			;[End Block]
		Case "room1_dead_end_ez"
			;[Block]
			Return(r_room1_dead_end_ez)
			;[End Block]
		Case "room1_lifts"
			;[Block]
			Return(r_room1_lifts)
			;[End Block]
		Case "room1_o5"
			;[Block]
			Return(r_room1_o5)
			;[End Block]
		Case "room2_ez"
			;[Block]
			Return(r_room2_ez)
			;[End Block]
		Case "room2_2_ez"
			;[Block]
			Return(r_room2_2_ez)
			;[End Block]
		Case "room2_3_ez"
			;[Block]
			Return(r_room2_3_ez)
			;[End Block]
		Case "room2_4_ez"
			;[Block]
			Return(r_room2_4_ez)
			;[End Block]
		Case "room2_5_ez"
			;[Block]
			Return(r_room2_5_ez)
			;[End Block]
		Case "room2_6_ez"
			;[Block]
			Return(r_room2_6_ez)
			;[End Block]
		Case "room2_bio"
			;[Block]
			Return(r_room2_bio)
			;[End Block]
		Case "room2_cafeteria"
			;[Block]
			Return(r_room2_cafeteria)
			;[End Block]
		Case "room2_ic"
			;[Block]
			Return(r_room2_ic)
			;[End Block]
		Case "room2_medibay"
			;[Block]
			Return(r_room2_medibay)
			;[End Block]
		Case "room2_office"
			;[Block]
			Return(r_room2_office)
			;[End Block]
		Case "room2_office_2"
			;[Block]
			Return(r_room2_office_2)
			;[End Block]
		Case "room2_office_3"
			;[Block]
			Return(r_room2_office_3)
			;[End Block]
		Case "room2_servers_ez"
			;[Block]
			Return(r_room2_servers_ez)
			;[End Block]
		Case "room2_scientists"
			;[Block]
			Return(r_room2_scientists)
			;[End Block]
		Case "room2_scientists_2"
			;[Block]
			Return(r_room2_scientists_2)
			;[End Block]
		Case "room2_tesla_ez"
			;[Block]
			Return(r_room2_tesla_ez)
			;[End Block]
		Case "cont2_860_1"
			;[Block]
			Return(r_cont2_860_1)
			;[End Block]
		Case "room2c_ez"
			;[Block]
			Return(r_room2c_ez)
			;[End Block]
		Case "room2c_2_ez"
			;[Block]
			Return(r_room2c_2_ez)
			;[End Block]
		Case "room2c_ec"
			;[Block]
			Return(r_room2c_ec)
			;[End Block]
		Case "room2c_gw_ez"
			;[Block]
			Return(r_room2c_gw_ez)
			;[End Block]
		Case "room3_gw"
			;[Block]
			Return(r_room3_gw)
			;[End Block]
		Case "room3_office"
			;[Block]
			Return(r_room3_office)
			;[End Block]
		Case "room3_ez"
			;[Block]
			Return(r_room3_ez)
			;[End Block]
		Case "room3_2_ez"
			;[Block]
			Return(r_room3_2_ez)
			;[End Block]
		Case "room3_3_ez"
			;[Block]
			Return(r_room3_3_ez)
			;[End Block]
		Case "room3_4_ez"
			;[Block]
			Return(r_room3_4_ez)
			;[End Block]
		Case "room4_ez"
			;[Block]
			Return(r_room4_ez)
			;[End Block]
		Case "dimension_106"
			;[Block]
			Return(r_dimension_106)
			;[End Block]
		Case "dimension_1499"
			;[Block]
			Return(r_dimension_1499)
			;[End Block]
		Default
			;[Block]
			Return(-1)
			;[End Block]
	End Select
End Function

Function CreateRoomTemplate.RoomTemplates(MeshPath$)
	Local rt.RoomTemplates
	
	rt.RoomTemplates = New RoomTemplates
	rt\OBJPath = "GFX\Map\Rooms\" + MeshPath
	rt\ID = RoomTempID
	RoomTempID = RoomTempID + 1
	
	Return(rt)
End Function

Function LoadRoomTemplates%(File$)
	CatchErrors("LoadRoomTemplates(" + File + ")")
	
	Local Loc$, i%
	Local rt.RoomTemplates = Null
	Local StrTemp$ = ""
	Local f% = OpenFile_Strict(File)
	
	While (Not Eof(f))
		Loc = Trim(ReadLine(f))
		If Left(Loc, 1) = "["
			Loc = Mid(Loc, 2, Len(Loc) - 2)
			If Loc <> "room ambience"
				StrTemp = IniGetString(File, Loc, "Mesh Path")
				
				rt.RoomTemplates = CreateRoomTemplate(StrTemp)
				rt\RoomID = FindRoomID(Lower(Loc))
				
				StrTemp = IniGetString(File, Loc, "Shape")
				
				Select(StrTemp)
					Case "room1", "1"
						;[Block]
						rt\Shape = ROOM1
						;[End Block]
					Case "room2", "2"
						;[Block]
						rt\Shape = ROOM2
						;[End Block]
					Case "room2C", "2C"
						;[Block]
						rt\Shape = ROOM2C
						;[End Block]
					Case "room3", "3"
						;[Block]
						rt\Shape = ROOM3
						;[End Block]
					Case "room4", "4"
						;[Block]
						rt\Shape = ROOM4
						;[End Block]
				End Select
				
				For i = 0 To 1
					StrTemp = IniGetString(File, Loc, "Zone" + (i + 1))
					
					Select(StrTemp)
						Case "LCZ"
							rt\Zone[i] = LCZ
						Case "HCZ"
							rt\Zone[i] = HCZ
						Case "EZ"
							rt\Zone[i] = EZ
						Case "RCZ"
							rt\Zone[i] = RCZ
						Case "BCZ"
							rt\Zone[i] = BCZ
					End Select
				Next
				
				rt\Commonness = Max(Min(IniGetInt(File, Loc, "Commonness"), 100), 0)
				rt\Large = IniGetInt(File, Loc, "Large")
				rt\DisableDecals = IniGetInt(File, Loc, "DisableDecals")
				rt\DisableOverlapCheck = IniGetInt(File, Loc, "DisableOverlapCheck")
			EndIf
		EndIf
	Wend
	
	i = 0
	Repeat
		StrTemp = IniGetString(File, "room ambience", "Ambience" + i)
		If StrTemp = "" Then Exit
		
		RoomAmbience[i] = LoadSound_Strict(StrTemp)
		i = i + 1
	Forever
	
	CloseFile(f)
	
	CatchErrors("Uncaught: LoadRoomTemplates(" + File + ")")
End Function

Function LoadRoomMesh%(rt.RoomTemplates)
	If FileExtension(rt\OBJPath) = "rmesh" ; ~ File is .rmesh
		rt\OBJ = LoadRMesh(rt\OBJPath, rt)
	ElseIf FileExtension(rt\OBJPath) = "b3d" ; ~ File is .b3d
		RuntimeError(Format(GetLocalString("runerr", "b3d"), rt\OBJPath))
	Else ; ~ File not found
		RuntimeError(Format(GetLocalString("runerr", "notfound"), rt\OBJPath))
	EndIf
	
	If rt\OBJ = 0 Then RuntimeError(Format(GetLocalString("runerr", "failedload"), rt\OBJPath))
	
	CalculateRoomTemplateExtents(rt)
	
	HideEntity(rt\OBJ)
End Function

Function RemoveRoomTemplate%(rt.RoomTemplates)
	FreeEntity(rt\OBJ) : rt\OBJ = 0
	Delete(rt)
End Function

;Type TriggerBox
;	Field OBJ%
;	Field Name$
;	Field MinX#, MinY#, MinZ#
;	Field MaxX#, MaxY#, MaxZ#
;End Type

; ~ Room Objects Constants
;[Block]
Const MaxRoomObjects% = 30
Const MaxRoomLevers% = 10
Const MaxRoomDoors% = 8
Const MaxRoomNPCs% = 12
Const MaxRoomSecurityCams% = 8
Const MaxRoomEmitters% = 8
Const MaxRoomAdjacents% = 4
Const MaxRoomTextures% = 8
Const MaxRoomAlarmRotors% = 2
Const MaxRoomFluLights% = 10
Const MaxRoomProjectors% = 2
;Const MaxRoomTriggerBoxes% = 8
;[End Block]

Type Rooms
	Field Zone%
	Field Found%
	Field OBJ%
	Field x#, y#, z#
	Field Angle%
	Field RoomTemplate.RoomTemplates
	Field Dist#
	Field SoundCHN%
	Field fr.Forest
	Field Objects%[MaxRoomObjects], ScriptedObject%[MaxRoomObjects]
	Field RoomLevers.Levers[MaxRoomLevers]
	Field RoomDoors.Doors[MaxRoomDoors]
	Field RoomAlarmRotors.AlarmRotors[MaxRoomAlarmRotors]
	Field RoomFluLights.FluLights[MaxRoomFluLights]
	Field RoomProjectors.Projectors[MaxRoomProjectors]
	Field NPC.NPCs[MaxRoomNPCs]
	Field RoomSecurityCams.SecurityCams[MaxRoomSecurityCams]
	Field RoomEmitters.Emitters[MaxRoomEmitters]
	Field mt.MTGrid
	Field Adjacent.Rooms[MaxRoomAdjacents]
	Field AdjDoor.Doors[MaxRoomAdjacents]
	Field Textures%[MaxRoomTextures]
	;Field TriggerBoxAmount%
	;Field TriggerBoxes.TriggerBox[MaxRoomTriggerBoxes]
	Field MaxWayPointY#
	Field MinX#, MinY#, MinZ#
	Field MaxX#, MaxY#, MaxZ#
	Field HiddenAlpha% = True
	Field RoomCenter%
End Type

Global PlayerRoom.Rooms

Const MTGridSize% = 19 ; ~ Same size as the main map itself (better for the map creator)
Const MTGridY# = 8.0

; ~ MT Model ID Constants
;[Block]
Const MT_ROOM2C% = 0
Const MT_ROOM1% = 1
Const MT_ROOM2% = 2
Const MT_ROOM3% = 3
Const MT_ROOM4% = 4
Const MT_FIRST_ELEVATOR% = 5
Const MT_SECOND_ELEVATOR% = 6
Const MT_GENERATOR% = 7
;[End Block]

;[Block]
Type MTGrid
	Field Grid%[PowTwo(MTGridSize)]
	Field Angles%[PowTwo(MTGridSize)]
	Field Meshes%[MaxMTModelIDAmount]
	Field Entities%[PowTwo(MTGridSize)]
	Field waypoints.WayPoints[PowTwo(MTGridSize)]
End Type
;[End Block]

Function UpdateMT%(mt.MTGrid)
	CatchErrors("UpdateMT()")
	
	Local tX%, tY%
	Local Dist#
	
	For tX = 0 To MTGridSize - 1
		For tY = 0 To MTGridSize - 1
			If mt\Entities[tX + (tY * MTGridSize)] <> 0
				Local PlayerPosY# = EntityY(me\Collider, True)
				Local TunnelPosY# = EntityY(mt\Entities[tX + (tY * MTGridSize)], True)
				
				If Abs(PlayerPosY - TunnelPosY) > 4.0 Then Exit
				
				Dist = Sqr(PowTwo(EntityX(me\Collider, True) - EntityX(mt\Entities[tX + (tY * MTGridSize)], True)) + PowTwo(PlayerPosY - TunnelPosY) + PowTwo(EntityZ(me\Collider, True) - EntityZ(mt\Entities[tX + (tY * MTGridSize)], True)))
				
				If Dist < opt\CameraFogFar * LightVolume * 1.2
					If EntityHidden(mt\Entities[tX + (tY * MTGridSize)]) Then ShowEntity(mt\Entities[tX + (tY * MTGridSize)])
				Else
					If (Not EntityHidden(mt\Entities[tX + (tY * MTGridSize)])) Then HideEntity(mt\Entities[tX + (tY * MTGridSize)])
				EndIf
			EndIf
		Next
	Next
	
	CatchErrors("Uncaught: UpdateMT()")
End Function

Function DestroyMT%(mt.MTGrid, DestroyWaypoint% = True)
	Local x%, y%
	
	For x = 0 To MTGridSize - 1
		For y = 0 To MTGridSize - 1
			If mt\Entities[x + (y * MTGridSize)] <> 0 Then FreeEntity(mt\Entities[x + (y * MTGridSize)]) : mt\Entities[x + (y * MTGridSize)] = 0
			If DestroyWaypoint And mt\waypoints[x + (y * MTGridSize)] <> Null Then RemoveWaypoint(mt\waypoints[x + (y * MTGridSize)]) : mt\waypoints[x + (y * MTGridSize)] = Null
		Next
	Next
	For x = 0 To MaxMTModelIDAmount - 1
		If mt\Meshes[x] <> 0 Then FreeEntity(mt\Meshes[x]) : mt\Meshes[x] = 0
	Next
End Function

Function PlaceMapCreatorMT%(r.Rooms)
	CatchErrors("PlaceMapCreatorMT()")
	
	Local dr.Doors, it.Items, wayp.WayPoints
	Local x%, y%, i%, Dist#
	Local Meshes%[MaxMTModelIDAmount]
	Local SinValue#, CosValue#
	
	For i = 0 To MaxMTModelIDAmount - 1
		Meshes[i] = CopyEntity(misc_I\MTModelID[i])
		HideEntity(Meshes[i])
	Next
	
	For y = 0 To MTGridSize - 1
		For x = 0 To MTGridSize - 1
			If r\mt\Grid[x + (y * MTGridSize)] > 0
				Local Tile_Type% = 0
				Local Angle# = 0.0
				
				Tile_Type = r\mt\Grid[x + (y * MTGridSize)]
				Angle = r\mt\Angles[x + (y * MTGridSize)] * 90.0
				
				Local Tile_Entity% = CopyEntity(Meshes[Tile_Type - 1])
				
				RotateEntity(Tile_Entity, 0.0, Angle, 0.0)
				ScaleEntity(Tile_Entity, RoomScale, RoomScale, RoomScale, True)
				PositionEntity(Tile_Entity, r\x + (x * 2.0), r\y + MTGridY, r\z + (y * 2.0), True)
				SinValue = Sin(EntityYaw(Tile_Entity, True))
				CosValue = Cos(EntityYaw(Tile_Entity, True))
				
				Select(Tile_Type)
					Case 1, 2
						;[Block]
						AddLight(r, r\x + (x * 2.0), r\y + MTGridY + (409.0 * RoomScale), r\z + (y * 2.0), 2, 0.25, 255, 200, 200)
						;[End Block]
					Case 3, 4, 5
						;[Block]
						AddLight(r, r\x + (x * 2.0), r\y + MTGridY + (424.0 * RoomScale), r\z + (y * 2.0), 2, 0.25, 255, 200, 200)
						;[End Block]
					Case 6
						;[Block]
						AddLight(r, r\x + (x * 2.0), r\y + MTGridY + (409.0 * RoomScale), r\z + (y * 2.0), 2, 0.25, 255, 200, 200)
						AddLight(r, r\x + (x * 2.0) + (CosValue * 560.0 * RoomScale), r\y + MTGridY + (469.0 * RoomScale), r\z + (y * 2.0) + (SinValue * 560.0 * RoomScale), 2, 0.25, 255, 200, 200)
						CreateProp(r, "GFX\map\Props\lamp3.b3d", r\x + (x * 2.0) + (SinValue * 254.0 * RoomScale) + (CosValue * 560.0 * RoomScale), r\y + MTGridY + (432.0 * RoomScale), (y * 2.0) + (CosValue * 254.0 * RoomScale) + (SinValue * 560.0 * RoomScale), 0.0, 90.0, 90.0, 400.0, 400.0, 400.0, False, 0, "")
						CreateProp(r, "GFX\map\Props\lamp3.b3d", r\x + (x * 2.0) - (SinValue * 254.0 * RoomScale) + (CosValue * 560.0 * RoomScale), r\y + MTGridY + (432.0 * RoomScale), (y * 2.0) - (CosValue * 254.0 * RoomScale) + (SinValue * 560.0 * RoomScale), 0.0, -90.0, 90.0, 400.0, 400.0, 400.0, False, 0, "")
						
						dr.Doors = CreateDoor(Null, r\x + (x * 2.0) + (CosValue * 256.0 * RoomScale), r\y + MTGridY, r\z + (y * 2.0) + (SinValue * 256.0 * RoomScale), EntityYaw(Tile_Entity, True) - 90.0, False, MT_ELEVATOR_DOOR)
						PositionEntity(dr\ElevatorPanel[1], EntityX(dr\ElevatorPanel[1], True) + (CosValue * 0.05), EntityY(dr\ElevatorPanel[1], True) + 0.1, EntityZ(dr\ElevatorPanel[1], True) + (SinValue * (-0.28)), True)
						RotateEntity(dr\ElevatorPanel[1], EntityPitch(dr\ElevatorPanel[1], True) + 45.0, EntityYaw(dr\ElevatorPanel[1], True), EntityRoll(dr\ElevatorPanel[1], True), True)
						
						Local TempInt2% = CreatePivot()
						
						RotateEntity(TempInt2, 0.0, EntityYaw(Tile_Entity, True) + 180.0, 0.0, True)
						PositionEntity(TempInt2, r\x + (x * 2.0) + (CosValue * 552.0 * RoomScale), r\y + MTGridY + (240.0 * RoomScale), r\z + (y * 2.0) + (SinValue * 552.0 * RoomScale))
						If r\RoomDoors[1] = Null
							r\RoomDoors[1] = dr
							r\Objects[3] = TempInt2
							PositionEntity(r\Objects[0], r\x + (x * 2.0), r\y + MTGridY, r\z + (y * 2.0), True)
						ElseIf r\RoomDoors[1] <> Null And r\RoomDoors[3] = Null
							r\RoomDoors[3] = dr
							r\Objects[5] = TempInt2
							PositionEntity(r\Objects[1], r\x + (x * 2.0), r\y + MTGridY, r\z + (y * 2.0), True)
						EndIf
						;[End Block]
					Case 7
						;[Block]
						AddLight(r, r\x + (x * 2.0) - (SinValue * 521.0 * RoomScale) + (CosValue * 16.0 * RoomScale), r\y + MTGridY + (396.0 * RoomScale), r\z + (y * 2.0) + (CosValue * 521.0 * RoomScale) + (SinValue * 16.0 * RoomScale), 2, 0.425, 255, 200, 200)
						CreateProp(r, "GFX\map\Props\tank2.b3d", r\x + (x * 2.0) - (SinValue * 369.0 * RoomScale) + (CosValue * 320.0 * RoomScale), r\y + MTGridY - (144.0 * RoomScale), r\z + (y * 2.0) + (CosValue * 369.0 * RoomScale) + (SinValue * 320.0 * RoomScale), 0.0, 0.0, 0.0, 3.0, 3.0, 3.0, True, 0, "")
						CreateProp(r, "GFX\map\Props\tank2.b3d", r\x + (x * 2.0) - (SinValue * 977.0 * RoomScale) + (CosValue * 320.0 * RoomScale), r\y + MTGridY - (144.0 * RoomScale), r\z + (y * 2.0) + (CosValue * 977.0 * RoomScale) + (SinValue * 320.0 * RoomScale), 0.0, 0.0, 0.0, 3.0, 3.0, 3.0, True, 0, "")
						
						it.Items = CreateItem("SCP-500-01", "scp500pill", r\x + (x * 2.0) + (CosValue * (-208.0) * RoomScale) - (SinValue * 1226.0 * RoomScale), r\y + MTGridY + (90.0 * RoomScale), r\z + (y * 2.0) + (SinValue * (-208.0) * RoomScale) + (CosValue * 1226.0 * RoomScale))
						EntityType(it\Collider, HIT_ITEM)
						
						it.Items = CreateItem("Night Vision Goggles", "nvg", r\x + (x * 2.0) - (SinValue * 504.0 * RoomScale) + (CosValue * 16.0 * RoomScale), r\y + MTGridY + (90.0 * RoomScale), r\z + (y * 2.0) + (CosValue * 504.0 * RoomScale) + (SinValue * 16.0 * RoomScale))
						EntityType(it\Collider, HIT_ITEM)
						;[End Block]
				End Select
				
				r\mt\Entities[x + (y * MTGridSize)] = Tile_Entity
				wayp = CreateWaypoint(Null, r, r\x + (x * 2.0), r\y + MTGridY + 0.2, r\z + (y * 2.0))
				r\mt\waypoints[x + (y * MTGridSize)] = wayp
				
				If y < MTGridSize - 1
					If r\mt\waypoints[x + ((y + 1) * MTGridSize)] <> Null
						Dist = EntityDistance(r\mt\waypoints[x + (y * MTGridSize)]\OBJ, r\mt\waypoints[x + ((y + 1) * MTGridSize)]\OBJ)
						For i = 0 To 3
							If r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + ((y + 1) * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + ((y + 1) * MTGridSize)]
								r\mt\waypoints[x + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
						For i = 0 To 3
							If r\mt\waypoints[x + ((y + 1) * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + ((y + 1) * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + ((y + 1) * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								r\mt\waypoints[x + ((y + 1) * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
					EndIf
				EndIf
				If y > 0
					If r\mt\waypoints[x + ((y - 1) * MTGridSize)] <> Null
						Dist = EntityDistance(r\mt\waypoints[x + (y * MTGridSize)]\OBJ, r\mt\waypoints[x + ((y - 1) * MTGridSize)]\OBJ)
						For i = 0 To 3
							If r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + ((y - 1) * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + ((y - 1) * MTGridSize)]
								r\mt\waypoints[x + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
						For i = 0 To 3
							If r\mt\waypoints[x + ((y - 1) * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + ((y - 1) * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								r\mt\waypoints[x + ((y - 1) * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
					EndIf
				EndIf
				If x > 0
					If r\mt\waypoints[x - 1 + (y * MTGridSize)] <> Null
						Dist = EntityDistance(r\mt\waypoints[x + (y * MTGridSize)]\OBJ, r\mt\waypoints[x - 1 + (y * MTGridSize)]\OBJ)
						For i = 0 To 3
							If r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x - 1 + (y * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x - 1 + (y * MTGridSize)]
								r\mt\waypoints[x + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
						For i = 0 To 3
							If r\mt\waypoints[x - 1 + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x - 1 + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								r\mt\waypoints[x - 1 + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
					EndIf
				EndIf
				If x < MTGridSize - 1
					If r\mt\waypoints[x + 1 + (y * MTGridSize)] <> Null
						Dist = EntityDistance(r\mt\waypoints[x + (y * MTGridSize)]\OBJ, r\mt\waypoints[x + 1 + (y * MTGridSize)]\OBJ)
						For i = 0 To 3
							If r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + 1 + (y * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + 1 + (y * MTGridSize)]
								r\mt\waypoints[x + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
						For i = 0 To 3
							If r\mt\waypoints[x + 1 + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + 1 + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								r\mt\waypoints[x + 1 + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
					EndIf
				EndIf
			EndIf
		Next
	Next
	
	For i = 0 To MaxMTModelIDAmount - 1
		r\mt\Meshes[i] = Meshes[i]
	Next
	
	CatchErrors("Uncaught: PlaceMapCreatorMT()")
End Function

Function CreateRoom.Rooms(Zone%, RoomShape%, x#, y#, z#, RoomID% = -1, Angle# = 0.0)
	CatchErrors("CreateRoom.Rooms(" + RoomShape + ", " + x + ", " + y + ", " + z + ", " + RoomID + ")")
	
	Local r.Rooms, rt.RoomTemplates
	Local i%
	
	r.Rooms = New Rooms
	r\Zone = Zone
	r\x = x : r\y = y : r\z = z
	
	If RoomID <> -1
		For rt.RoomTemplates = Each RoomTemplates
			If rt\RoomID = RoomID
				r\RoomTemplate = rt
				
				If rt\OBJ = 0 Then LoadRoomMesh(rt)
				
				r\OBJ = CopyEntity(rt\OBJ)
				ScaleEntity(r\OBJ, RoomScale, RoomScale, RoomScale)
				EntityType(r\OBJ, HIT_MAP)
				EntityPickMode(r\OBJ, 2)
				PositionEntity(r\OBJ, x, y, z)
				
				For i = 0 To MaxRoomObjects - 1
					r\ScriptedObject[i] = False
				Next
				FillRoom(r)
				
				r\Angle = Angle
				RotateEntity(r\OBJ, 0.0, Angle, 0.0)
				
				Return(r)
			EndIf
		Next
	EndIf
	
	Local Temp% = 0
	
	For rt.RoomTemplates = Each RoomTemplates
		For i = 0 To 4
			If rt\Zone[i] = Zone
				If rt\Shape = RoomShape
					Temp = Temp + rt\Commonness
					Exit
				EndIf
			EndIf
		Next
	Next
	
	Local RandomRoom% = Rand(Temp)
	
	Temp = 0
	For rt.RoomTemplates = Each RoomTemplates
		For i = 0 To 4
			If rt\Zone[i] = Zone And rt\Shape = RoomShape
				Temp = Temp + rt\Commonness
				If RandomRoom > Temp - rt\Commonness And RandomRoom <= Temp
					r\RoomTemplate = rt
					
					If rt\OBJ = 0 Then LoadRoomMesh(rt)
					
					r\OBJ = CopyEntity(rt\OBJ)
					ScaleEntity(r\OBJ, RoomScale, RoomScale, RoomScale)
					EntityType(r\OBJ, HIT_MAP)
					EntityPickMode(r\OBJ, 2)
					PositionEntity(r\OBJ, x, y, z)
					
					For i = 0 To MaxRoomObjects - 1
						r\ScriptedObject[i] = False
					Next
					FillRoom(r)
					
					r\Angle = Angle
					RotateEntity(r\OBJ, 0.0, Angle, 0.0)
					
					Return(r)
				EndIf
			EndIf
		Next
	Next
	
	CatchErrors("Uncaught: CreateRoom.Rooms(" + RoomShape + ", " + x + ", " + y + ", " + z + ", " + RoomID + "))")
End Function

Function RemoveRoom%(r.Rooms)
	Local i%
	
	For i = 0 To MaxRoomTextures - 1
		r\Textures[i] = 0
	Next
	For i = 0 To MaxRoomObjects - 1
		If r\Objects[i] <> 0 Then EntityParent(r\Objects[i], 0)
	Next
	For i = 0 To MaxRoomObjects - 1
		If r\Objects[i] <> 0 Then FreeEntity(r\Objects[i]) : r\Objects[i] = 0
	Next
	
	If r\RoomCenter <> 0 Then FreeEntity(r\RoomCenter) : r\RoomCenter = 0
	FreeEntity(r\OBJ) : r\OBJ = 0
	Delete(r)
End Function

Type TempWayPoints
	Field x#, y#, z#
	Field RoomTemplate.RoomTemplates
End Type

Type WayPoints
	Field OBJ%
	Field door.Doors
	Field room.Rooms
	Field State%
	Field connected.WayPoints[5]
	Field Dist#[5]
	Field Fcost#, Gcost#, Hcost#
	Field parent.WayPoints
End Type

Function CreateWaypoint.WayPoints(door.Doors, room.Rooms, x#, y#, z#)
	Local w.WayPoints
	
	w.WayPoints = New WayPoints
	w\OBJ = CreatePivot()
	PositionEntity(w\OBJ, x, y, z)
	If room <> Null Then EntityParent(w\OBJ, room\OBJ)
	
	w\room = room
	w\door = door
	
	Return(w)
End Function

Function RemoveWaypoint%(w.WayPoints)
	FreeEntity(w\OBJ) : w\OBJ = 0
	Delete(w)
End Function

Include "Source Code\Doors_Core.bb"

Global PlayerInsideElevator% = False
Global PlayerElevator% = 0

;[Block]
Type ElevatorInstance
	Field ButtonTexture%[0]
End Type
;[End Block]

Global elev_I.ElevatorInstance

Type Elevator
	Field Obj%
	Field State#
	Field CurrFloor%
	Field ToFloor%
	Field Door.Doors
	Field FloorY#[3]
	Field ID%
	Field Speed#
	Field CurrSpeed#
	Field Sound%
	Field SoundCHN%
	Field CurrSound%
	Field Room.Rooms
	Field Button_Arrow[2]
	Field Button_Numbers
	Field FloorLocked[3]
End Type

Function CreateElevator.Elevator(Obj%, CurrFloor%, Door.Doors, ID%, Room.Rooms, Floor1Y#, Floor2Y#, Floor3Y#, Speed# = 6.0)
	Local elev.Elevator = New Elevator
	Local Tex%, i%
	
	elev\Room = Room
	elev\Obj = Obj
	elev\CurrFloor = CurrFloor
	elev\ToFloor = CurrFloor
	elev\State = 0.0
	elev\Door = Door
	elev\FloorY[0] = Floor1Y
	elev\FloorY[1] = Floor2Y
	elev\FloorY[2] = Floor3Y
	elev\Speed = Speed
	elev\ID = ID
	
	Tex = LoadTexture_Strict("GFX\HUD\Elevator_Arrow.png", 1+2)
	elev\Button_Arrow[0] = CreateSprite(elev\Door\Buttons[0])
	elev\Button_Arrow[1] = CopyEntity(elev\Button_Arrow[0], elev\Door\Buttons[0])
	For i = 0 To 1
		SpriteViewMode elev\Button_Arrow[i], 2
		ScaleSprite elev\Button_Arrow[i], 0.25, 0.25
		EntityFX elev\Button_Arrow[i], 1
		EntityBlend elev\Button_Arrow[i], 3
		EntityTexture elev\Button_Arrow[i], Tex
	Next
	PositionEntity elev\Button_Arrow[0], 0.7, 2.9, -1.3
	PositionEntity elev\Button_Arrow[1], -0.7, 2.9, -1.3
	TurnEntity elev\Button_Arrow[1], 0, 0, 180
	DeleteSingleTextureEntryFromCache Tex
	elev\Button_Numbers = CreateSprite(elev\Door\Buttons[0])
	ScaleSprite elev\Button_Numbers, 0.5, 0.5
	SpriteViewMode elev\Button_Numbers, 2
	PositionEntity elev\Button_Numbers, 0, 2.7, -1.3
	EntityFX elev\Button_Numbers, 1
	EntityBlend elev\Button_Numbers, 3
	
	Return(elev)
End Function

Function StartNewElevator(Door.Doors, NewFloor%)
	Local elev.Elevator, elev_found.Elevator
	Local PlayerInside% = False
	
	For elev = Each Elevator
		If elev\Door = Door
			elev_found = elev
			Exit
		EndIf
	Next
	
	If elev\Door\Locked Then
		CreateMsg(GetLocalString("msg", "button.locked"))
	ElseIf elev\FloorLocked[NewFloor - 1] Then
		CreateMsg(GetLocalString("msg", "button.nothappend"))
	Else
		If NewFloor <> elev\CurrFloor
			elev\ToFloor = NewFloor
			If Abs(EntityX(me\Collider)-EntityX(elev\Obj,True))<=280.0*RoomScale+(0.015*fps\Factor[0])
				If Abs(EntityZ(me\Collider)-EntityZ(elev\Obj,True))<=280.0*RoomScale+(0.015*fps\Factor[0])
					PlayerInsideElevator = True
					PlayerElevator = elev\ID
					PlayerInside = True
				EndIf
			EndIf
			If (Not PlayerInside)
				CreateMsg(GetLocalString("msg", "elev.called"))
			Else
				OpenCloseDoor(elev\Door)
			EndIf
		Else
			CreateMsg(GetLocalString("msg", "elev.floor"))
		EndIf
	EndIf
	
End Function

Function UpdateElevators()
	Local elev.Elevator
	Local n.NPCs, it.Items
	Local i%
	
	For elev = Each Elevator
		If elev\ToFloor <> elev\CurrFloor Then
			If elev\State < 200.0 Then
				elev\State = elev\State + fps\Factor[0]
			Else
				If elev\CurrSound=0 Then
					If elev\SoundCHN <> 0 Then StopStream_Strict(elev\SoundCHN)
					
					If (Not PlayerInsideElevator) Then
						
					Else
						elev\SoundCHN = StreamSound_Strict("SFX\General\Elevator\StartAndLoop"+Rand(1,3)+".ogg",opt\SFXVolume*0.5,0)
					EndIf
					elev\CurrSound = 1
				Else
					If elev\CurrSound = 1 Lor (elev\CurrSound = 2 And (Not IsStreamPlaying_Strict(elev\SoundCHN))) Then
						If (Not IsStreamPlaying_Strict(elev\SoundCHN))
							StopStream_Strict(elev\SoundCHN)
							elev\SoundCHN = StreamSound_Strict("SFX\General\Elevator\Loop"+Rand(1,3)+".ogg",opt\SFXVolume*0.5,Mode)
							elev\CurrSound = 2
						EndIf
					EndIf
				EndIf
				If elev\ToFloor < elev\CurrFloor Then
					ShowEntity elev\Button_Arrow[1]
					HideEntity elev\Button_Arrow[0]
					If elev\CurrFloor = 3 And elev\ToFloor = 1 Then
						If EntityY(elev\Obj) < elev\FloorY[1] Then
							EntityTexture elev\Button_Numbers,elev_I\ButtonTexture[0], 1
						Else
							EntityTexture elev\Button_Numbers,elev_I\ButtonTexture[0], 2
						EndIf
					Else
						If elev\CurrFloor = 3 Then
							EntityTexture elev\Button_Numbers,elev_I\ButtonTexture[0], 2
						Else
							EntityTexture elev\Button_Numbers,elev_I\ButtonTexture[0], 1
						EndIf
					EndIf
					If EntityY(elev\Obj) > elev\FloorY[elev\ToFloor-1] Then
						If EntityY(elev\Obj) > elev\FloorY[elev\ToFloor-1] + (0.6/RoomScale) Then
							elev\CurrSpeed = CurveValue(elev\Speed,elev\CurrSpeed,75.0)
						Else
							elev\CurrSpeed = CurveValue(0.05,elev\CurrSpeed,25.0)
							If elev\CurrSound = 1 Lor elev\CurrSound = 2 Then
								StopStream_Strict(elev\SoundCHN)
								elev\SoundCHN = StreamSound_Strict("SFX\General\Elevator\Stop"+Rand(1,3)+".ogg",opt\SFXVolume*0.5,0)
								elev\CurrSound = 3
							EndIf
						EndIf
						MoveEntity elev\Obj, 0, -elev\Speed * fps\Factor[0], 0
						If elev\Room = PlayerRoom And PlayerInsideElevator And PlayerElevator = elev\ID Then
							TeleportEntity me\Collider,EntityX(me\Collider),EntityY(elev\Obj,True)+0.3,EntityZ(me\Collider)
						EndIf
					Else
						PositionEntity elev\Obj,EntityX(elev\Obj),elev\FloorY[elev\ToFloor-1],EntityZ(elev\Obj)
						elev\CurrFloor = elev\ToFloor
						elev\State = fps\Factor[0]
					EndIf
				Else
					ShowEntity elev\Button_Arrow[0]
					HideEntity elev\Button_Arrow[1]
					If elev\CurrFloor = 1 And elev\ToFloor = 3 Then
						If EntityY(elev\Obj)>elev\FloorY[1]
							EntityTexture elev\Button_Numbers,elev_I\ButtonTexture[0], 1
						Else
							EntityTexture elev\Button_Numbers,elev_I\ButtonTexture[0], 0
						EndIf
					Else
						If elev\CurrFloor = 1 Then
							EntityTexture elev\Button_Numbers,elev_I\ButtonTexture[0], 0
						Else
							EntityTexture elev\Button_Numbers,elev_I\ButtonTexture[0], 1
						EndIf
					EndIf
					If EntityY(elev\Obj) < elev\FloorY[elev\ToFloor-1] Then
						If EntityY(elev\Obj) < elev\FloorY[elev\ToFloor-1] - (0.6/RoomScale) Then
							elev\CurrSpeed = CurveValue(elev\Speed,elev\CurrSpeed,75.0)
						Else
							elev\CurrSpeed = CurveValue(0.05,elev\CurrSpeed,25.0)
							If elev\CurrSound = 1 Lor elev\CurrSound = 2 Then
								StopStream_Strict(elev\SoundCHN)
								elev\SoundCHN = StreamSound_Strict("SFX\General\Elevator\Stop" + Rand(1,3) + ".ogg", opt\SFXVolume*0.5, 0)
								elev\CurrSound = 3
							EndIf
						EndIf
						MoveEntity elev\Obj,0,elev\CurrSpeed*fps\Factor[0],0
						If elev\Room = PlayerRoom And PlayerInsideElevator And PlayerElevator = elev\ID Then
							TeleportEntity me\Collider,EntityX(me\Collider),EntityY(elev\Obj,True)+0.3,EntityZ(me\Collider)
						EndIf
					Else
						PositionEntity elev\Obj,EntityX(elev\Obj),elev\FloorY[elev\ToFloor-1],EntityZ(elev\Obj)
						elev\CurrFloor = elev\ToFloor
						elev\State = fps\Factor[0]
					EndIf
				EndIf
				If elev\Room = PlayerRoom And PlayerInsideElevator And PlayerElevator = elev\ID Then
					PositionEntity elev\Door\OBJ,EntityX(elev\Door\OBJ),EntityY(elev\Obj,True),EntityZ(elev\Door\OBJ)
					PositionEntity elev\Door\OBJ2,EntityX(elev\Door\OBJ2),EntityY(elev\Obj,True),EntityZ(elev\Door\OBJ2)
					PositionEntity elev\Door\FrameOBJ,EntityX(elev\Door\FrameOBJ),EntityY(elev\Obj,True),EntityZ(elev\Door\FrameOBJ)
					PositionEntity elev\Door\Buttons[0],EntityX(elev\Door\Buttons[0]),EntityY(elev\Obj,True)+0.6,EntityZ(elev\Door\Buttons[0])
					PositionEntity elev\Door\Buttons[1],EntityX(elev\Door\Buttons[1]),EntityY(elev\Obj,True)+0.7,EntityZ(elev\Door\Buttons[1])
;					PositionEntity elev\Door\ElevatorPanel[0],EntityX(elev\Door\ElevatorPanel[0]),EntityY(elev\Obj,True)+0.6,EntityZ(elev\Door\ElevatorPanel[0])
;					PositionEntity elev\Door\ElevatorPanel[1],EntityX(elev\Door\ElevatorPanel[1]),EntityY(elev\Obj,True)+0.7,EntityZ(elev\Door\ElevatorPanel[1])
					PositionEntity me\Collider,EntityX(me\Collider),EntityY(elev\Obj,True)+0.3,EntityZ(me\Collider)
					me\DropSpeed = 0
					me\CameraShake = Sin(Abs(elev\CurrSpeed*15)/3.0)*0.5
				EndIf
				For n.NPCs = Each NPCs
					If Abs(EntityX(n\Collider)-EntityX(elev\Obj,True)) < 280.0*RoomScale+(0.015*fps\Factor[0]) Then
						If Abs(EntityZ(n\Collider)-EntityZ(elev\Obj,True)) < 280.0*RoomScale+(0.015*fps\Factor[0]) Then
							PositionEntity n\Collider,EntityX(n\Collider),EntityY(elev\Obj,True)+n\CollRadius,EntityZ(n\Collider)
							n\DropSpeed = 0
						EndIf
					EndIf
				Next
				For it.Items = Each Items
					If Abs(EntityX(it\Collider)-EntityX(elev\Obj,True)) < 280.0*RoomScale+(0.015*fps\Factor[0]) Then
						If Abs(EntityZ(it\Collider)-EntityZ(elev\Obj,True)) < 280.0*RoomScale+(0.015*fps\Factor[0]) Then
							PositionEntity it\Collider,EntityX(it\Collider),EntityY(elev\Obj,True)+0.01,EntityZ(it\Collider)
							it\DropSpeed = 0
						EndIf
					EndIf
				Next
			EndIf
		Else
			elev\CurrSpeed = 0.0
			elev\CurrSound = 0
			For i = 0 To 1
				HideEntity elev\Button_Arrow[i]
			Next
			Select(elev\CurrFloor)
				Case 1
					EntityTexture elev\Button_Numbers,elev_I\ButtonTexture[0], 0
				Case 2
					EntityTexture elev\Button_Numbers,elev_I\ButtonTexture[0], 1
				Case 3
					EntityTexture elev\Button_Numbers,elev_I\ButtonTexture[0], 2
			End Select
			If elev\State > 0.0 And elev\State < 100.0 Then
				elev\State = elev\State + fps\Factor[0]
			ElseIf elev\State >= 100.0 Then
				PlaySound2(ElevatorBeepSFX, Camera, elev\Door\FrameOBJ, 6.0)
				OpenCloseDoor(elev\Door)
				PlayerInsideElevator = False
				elev\State = 0.0
			EndIf
		EndIf
		If PlayerInsideElevator Then
			SetStreamVolume_Strict(elev\SoundCHN,opt\SFXVolume*0.5)
		Else
			UpdateStreamSoundOrigin(elev\SoundCHN,Camera,elev\Obj)
		EndIf
	Next
	
End Function

Function DeleteElevator()
	Local elev.Elevator
	
	For elev = Each Elevator
		FreeEntity elev\Obj : elev\Obj=0
		StopStream_Strict(elev\SoundCHN)
		elev\SoundCHN = 0
		elev\Sound = 0
		Delete elev
	Next
	
End Function

Global ToMTElevatorFloor%, PlayerInsideMTElevator%

; ~ MT Elevator Floor ID Constants
;[Block]
Const LowerFloor% = -1
Const NullFloor% = 0
Const UpperFloor% = 1
Const Floor1499% = 2
;[End Block]

Function UpdateElevatorPanel%(d.Doors)
	Local TextureID%, i%
	
	If PlayerInsideMTElevator
		If InFacility = LowerFloor Lor (InFacility <> UpperFloor And ToMTElevatorFloor = UpperFloor)
			TextureID = ELEVATOR_PANEL_UP
		Else
			TextureID = ELEVATOR_PANEL_DOWN
		EndIf
	Else
		If InFacility = LowerFloor Lor (InFacility <> UpperFloor And ToMTElevatorFloor = UpperFloor)
			TextureID = ELEVATOR_PANEL_DOWN
		Else
			TextureID = ELEVATOR_PANEL_UP
		EndIf
	EndIf
	
	For i = 0 To 1
		If d\ElevatorPanel[i] <> 0 Then EntityTexture(d\ElevatorPanel[i], d_I\ElevatorPanelTextureID[TextureID])
	Next
End Function

Function ClearElevatorPanelTexture%(d.Doors)
	Local i%
	
	For i = 0 To 1
		If d\ElevatorPanel[i] <> 0 Then EntityTexture(d\ElevatorPanel[i], d_I\ElevatorPanelTextureID[ELEVATOR_PANEL_IDLE])
	Next
End Function

Function UpdateMTElevators#(State#, door1.Doors, door2.Doors, FirstPivot%, SecondPivot%, event.Events, IgnoreRotation% = True)
	Local n.NPCs, it.Items, de.Decals
	Local x#, z#, Dist#, Dir#, i%
	
	door1\IsElevatorDoor = 1
	door2\IsElevatorDoor = 1
	If door1\Open And (Not door2\Open) And door1\OpenState = 180.0 And State < 70.0 * 7.4
		State = -1.0
		door1\Locked = 0
		If (d_I\ClosestButton = door2\Buttons[0] Lor d_I\ClosestButton = door2\Buttons[1]) And HitKeyUse
			OpenCloseDoor(door1)
			UpdateElevatorPanel(door2)
		EndIf
	ElseIf door2\Open And (Not door1\Open) And door2\OpenState = 180.0
		State = 1.0
		door2\Locked = 0
		If (d_I\ClosestButton = door1\Buttons[0] Lor d_I\ClosestButton = door1\Buttons[1]) And HitKeyUse
			OpenCloseDoor(door2)
			UpdateElevatorPanel(door1)
		EndIf
	ElseIf Abs(door1\OpenState - door2\OpenState) < 0.2
		door1\IsElevatorDoor = 2
		door2\IsElevatorDoor = 2
	EndIf
	
	door1\Locked = 1
	door2\Locked = 1
	If door1\Open
		door1\IsElevatorDoor = 3
		If PlayerInsideMTElevator
			If State < 70.0 * 7.4 Then door1\Locked = 0
			door1\IsElevatorDoor = 1
		EndIf
	EndIf
	If door2\Open
		door2\IsElevatorDoor = 3
		If PlayerInsideMTElevator
			door2\Locked = 0
			door2\IsElevatorDoor = 1
		EndIf
	EndIf
	
	Local IsSceneTriggered% = False
	
	If n_I\Curr096 <> Null
		If n_I\Curr096\State > 1.0 And n_I\Curr096\Target = Null And (InFacility = NullFloor) And PlayerInsideMTElevator And (Not chs\NoTarget) Then IsSceneTriggered = True
	EndIf
	If (Not IsSceneTriggered)
		If (Not door1\Open) And (Not door2\Open)
			If PlayerInsideMTElevator Then CanSave = 0
			door1\Locked = 1
			door2\Locked = 1
			If door1\OpenState = 0.0 And door2\OpenState = 0.0
				Local PlayerX# = EntityX(me\Collider, True)
				Local PlayerY# = EntityY(me\Collider, True)
				Local PlayerZ# = EntityZ(me\Collider, True)
				Local FirstPivotX# = EntityX(FirstPivot, True)
				Local FirstPivotY# = EntityY(FirstPivot, True)
				Local FirstPivotZ# = EntityZ(FirstPivot, True)
				Local FirstPivotYaw# = EntityYaw(FirstPivot, True)
				Local SecondPivotX# = EntityX(SecondPivot, True)
				Local SecondPivotY# = EntityY(SecondPivot, True)
				Local SecondPivotZ# = EntityZ(SecondPivot, True)
				Local SecondPivotYaw# = EntityYaw(SecondPivot, True)
				Local Minus022# = (280.0 * RoomScale) - 0.22
				Local Plus022# = ((-280.0) * RoomScale) + 0.22
				Local FPSFactor01# = fps\Factor[0] * 0.1
				Local OBJPosX#, OBJPosY#, OBJPosZ#
				Local IsInside% = False
				
				If State < 0.0
					State = State - fps\Factor[0]
					IsInside = IsInsideMTElevator(PlayerX, PlayerY, PlayerZ, FirstPivot)
					If IsInside
						If (Not ChannelPlaying(door1\SoundCHN2)) Then door1\SoundCHN2 = PlaySound_Strict(ElevatorMoveSFX)
						
						me\CameraShake = Sin(Abs(State) / 3.0) * 0.3
						
						UpdateElevatorPanel(door1)
					EndIf
					
					If State < -500.0
						door1\Locked = 1
						door2\Locked = 0
						State = 0.0
						If IsInside
							If (Not IgnoreRotation)
								Dist = Distance(PlayerX, FirstPivotX, PlayerZ, FirstPivotZ)
								Dir = PointDirection(PlayerX, PlayerZ, FirstPivotX, FirstPivotZ)
								Dir = Dir + SecondPivotYaw - FirstPivotYaw
								Dir = WrapAngle(Dir)
								x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
								z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
								RotateEntity(me\Collider, EntityPitch(me\Collider, True), SecondPivotYaw + AngleDist(EntityYaw(me\Collider, True), FirstPivotYaw), EntityRoll(me\Collider, True), True)
							Else
								x = Max(Min((PlayerX - FirstPivotX), Minus022), Plus022)
								z = Max(Min((PlayerZ - FirstPivotZ), Minus022), Plus022)
							EndIf
							
							TeleportEntity(me\Collider, SecondPivotX + x, FPSFactor01 + SecondPivotY + (PlayerY - FirstPivotY), SecondPivotZ + z, 0.3, True)
							me\DropSpeed = 0.0
							UpdateLightsTimer = 0.0
							UpdateLightVolume()
							UpdateDoors()
							UpdateRooms()
							
							door1\SoundCHN = PlaySound2(OpenDoorSFX(MT_ELEVATOR_DOOR, Rand(0, 2)), Camera, door1\OBJ)
						EndIf
						
						For n.NPCs = Each NPCs
							OBJPosX = EntityX(n\Collider, True) : OBJPosY = EntityY(n\Collider, True) : OBJPosZ = EntityZ(n\Collider, True)
							If IsInsideMTElevator(OBJPosX, OBJPosY, OBJPosZ, FirstPivot)
								If (Not IgnoreRotation)
									Dist = Distance(OBJPosX, FirstPivotX, OBJPosZ, FirstPivotZ)
									Dir = PointDirection(OBJPosX, OBJPosZ, FirstPivotX, FirstPivotZ)
									Dir = Dir + SecondPivotYaw - FirstPivotYaw
									Dir = WrapAngle(Dir)
									x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
									z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
									RotateEntity(n\Collider, EntityPitch(n\Collider, True), SecondPivotYaw + AngleDist(EntityYaw(n\Collider, True), FirstPivotYaw), EntityRoll(n\Collider, True), True)
								Else
									x = Max(Min((OBJPosX - FirstPivotX), Minus022), Plus022)
									z = Max(Min((OBJPosZ - FirstPivotZ), Minus022), Plus022)
								EndIf
								TeleportEntity(n\Collider, SecondPivotX + x, FPSFactor01 + SecondPivotY + (OBJPosY - FirstPivotY), SecondPivotZ + z, n\CollRadius, True)
							EndIf
						Next
						
						For it.Items = Each Items
							OBJPosX = EntityX(it\Collider, True) : OBJPosY = EntityY(it\Collider, True) : OBJPosZ = EntityZ(it\Collider, True)
							If IsInsideMTElevator(OBJPosX, OBJPosY, OBJPosZ, FirstPivot)
								If (Not IgnoreRotation)
									Dist = Distance(OBJPosX, FirstPivotX, OBJPosZ, FirstPivotZ)
									Dir = PointDirection(OBJPosX, OBJPosZ, FirstPivotX, FirstPivotZ)
									Dir = Dir + SecondPivotYaw - FirstPivotYaw
									Dir = WrapAngle(Dir)
									x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
									z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
									RotateEntity(it\Collider, EntityPitch(it\Collider, True), SecondPivotYaw + AngleDist(EntityYaw(it\Collider, True), FirstPivotYaw), EntityRoll(it\Collider, True), True)
								Else
									x = Max(Min((OBJPosX - FirstPivotX), Minus022), Plus022)
									z = Max(Min((OBJPosZ - FirstPivotZ), Minus022), Plus022)
								EndIf
								TeleportEntity(it\Collider, SecondPivotX + x, FPSFactor01 + SecondPivotY + (OBJPosY - FirstPivotY), SecondPivotZ + z, 0.01, True)
								it\DistTimer = 0.0
								UpdateItems()
							EndIf
						Next
						
						For de.Decals = Each Decals
							OBJPosX = EntityX(de\OBJ, True) : OBJPosY = EntityY(de\OBJ, True) : OBJPosZ = EntityZ(de\OBJ, True)
							If IsInsideMTElevator(OBJPosX, OBJPosY, OBJPosZ, FirstPivot)
								If (Not IgnoreRotation)
									Dist = Distance(OBJPosX, FirstPivotX, EntityZ(de\OBJ, True), FirstPivotZ)
									Dir = PointDirection(OBJPosX, EntityZ(de\OBJ, True), FirstPivotX, FirstPivotZ)
									Dir = Dir + SecondPivotYaw - FirstPivotYaw
									Dir = WrapAngle(Dir)
									x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
									z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
									RotateEntity(de\OBJ, EntityPitch(de\OBJ, True), SecondPivotYaw + AngleDist(EntityYaw(de\OBJ, True), FirstPivotYaw), EntityRoll(de\OBJ, True), True)
								Else
									x = Max(Min((OBJPosX - FirstPivotX), Minus022), Plus022)
									z = Max(Min((OBJPosZ - FirstPivotZ), Minus022), Plus022)
								EndIf
								TeleportEntity(de\OBJ, SecondPivotX + x, FPSFactor01 + SecondPivotY + (OBJPosY - FirstPivotY), SecondPivotZ + z, -0.01, True)
								UpdateDecals()
							EndIf
						Next
						OpenCloseDoor(door2, (Not PlayerInsideMTElevator))
						door1\Open = False
						
						; ~ Return to default panel texture
						ClearElevatorPanelTexture(door1)
						ClearElevatorPanelTexture(door2)
						PlaySound2(ElevatorBeepSFX, Camera, FirstPivot, 4.0)
					EndIf
				Else
					State = State + fps\Factor[0]
					IsInside = IsInsideMTElevator(PlayerX, PlayerY, PlayerZ, SecondPivot)
					If IsInside
						If (Not ChannelPlaying(door2\SoundCHN2)) Then door2\SoundCHN2 = PlaySound_Strict(ElevatorMoveSFX)
						
						me\CameraShake = Sin(Abs(State) / 3.0) * 0.3
						
						UpdateElevatorPanel(door2)
					EndIf
					
					If State > 500.0
						door1\Locked = 0
						door2\Locked = 1
						State = 0.0
						If IsInside
							If (Not IgnoreRotation)
								Dist = Distance(PlayerX, SecondPivotX, PlayerZ, SecondPivotZ)
								Dir = PointDirection(PlayerX, PlayerZ, SecondPivotX, SecondPivotZ)
								Dir = Dir + FirstPivotYaw - SecondPivotYaw
								x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
								z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
								RotateEntity(me\Collider, EntityPitch(me\Collider, True), SecondPivotYaw + AngleDist(EntityYaw(me\Collider, True), FirstPivotYaw), EntityRoll(me\Collider, True), True)
							Else
								x = Max(Min((PlayerX - SecondPivotX), Minus022), Plus022)
								z = Max(Min((PlayerZ - SecondPivotZ), Minus022), Plus022)
							EndIf
							TeleportEntity(me\Collider, FirstPivotX + x, FPSFactor01 + FirstPivotY + (PlayerY - SecondPivotY), FirstPivotZ + z, 0.3, True)
							me\DropSpeed = 0.0
							UpdateLightsTimer = 0.0
							UpdateLightVolume()
							UpdateDoors()
							UpdateRooms()
							
							door2\SoundCHN = PlaySound2(OpenDoorSFX(MT_ELEVATOR_DOOR, Rand(0, 2)), Camera, door2\OBJ)
						EndIf
						
						For n.NPCs = Each NPCs
							OBJPosX = EntityX(n\Collider, True) : OBJPosY = EntityY(n\Collider, True) : OBJPosZ = EntityZ(n\Collider, True)
							If IsInsideMTElevator(OBJPosX, OBJPosY, OBJPosZ, SecondPivot)
								If (Not IgnoreRotation)
									Dist = Distance(OBJPosX, SecondPivotX, OBJPosZ, SecondPivotZ)
									Dir = PointDirection(OBJPosX, OBJPosZ, SecondPivotX, SecondPivotZ)
									Dir = Dir + FirstPivotYaw - SecondPivotYaw
									x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
									z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
									RotateEntity(n\Collider, EntityPitch(n\Collider, True), SecondPivotYaw + AngleDist(EntityYaw(n\Collider, True), FirstPivotYaw), EntityRoll(n\Collider, True), True)
								Else
									x = Max(Min((OBJPosX - SecondPivotX), Minus022), Plus022)
									z = Max(Min((OBJPosZ - SecondPivotZ), Minus022), Plus022)
								EndIf
								TeleportEntity(n\Collider, FirstPivotX + x, FPSFactor01 + FirstPivotY + (OBJPosY - SecondPivotY), FirstPivotZ + z, n\CollRadius, True)
							EndIf
						Next
						
						For it.Items = Each Items
							OBJPosX = EntityX(it\Collider, True) : OBJPosY = EntityY(it\Collider, True) : OBJPosZ = EntityZ(it\Collider, True)
							If IsInsideMTElevator(OBJPosX, OBJPosY, OBJPosZ, SecondPivot)
								If (Not IgnoreRotation)
									Dist = Distance(OBJPosX, SecondPivotX, OBJPosZ, SecondPivotZ)
									Dir = PointDirection(OBJPosX, OBJPosZ, SecondPivotX, SecondPivotZ)
									Dir = Dir + FirstPivotYaw - SecondPivotYaw
									x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
									z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
									RotateEntity(it\Collider, EntityPitch(it\Collider, True), SecondPivotYaw + AngleDist(EntityYaw(it\Collider, True), FirstPivotYaw), EntityRoll(it\Collider, True), True)
								Else
									x = Max(Min((OBJPosX - SecondPivotX), Minus022), Plus022)
									z = Max(Min((OBJPosZ - SecondPivotZ), Minus022), Plus022)
								EndIf
								TeleportEntity(it\Collider, FirstPivotX + x, FPSFactor01 + FirstPivotY + (OBJPosY - SecondPivotY), FirstPivotZ + z, 0.01, True)
								it\DistTimer = 0.0
								UpdateItems()
							EndIf
						Next
						
						For de.Decals = Each Decals
							OBJPosX = EntityX(de\OBJ, True) : OBJPosY = EntityY(de\OBJ, True) : OBJPosZ = EntityZ(de\OBJ, True)
							If IsInsideMTElevator(OBJPosX, OBJPosY, OBJPosZ, SecondPivot)
								If (Not IgnoreRotation)
									Dist = Distance(OBJPosX, SecondPivotX, OBJPosZ, SecondPivotZ)
									Dir = PointDirection(OBJPosX, OBJPosZ, SecondPivotX, SecondPivotZ)
									Dir = Dir + FirstPivotYaw - SecondPivotYaw
									x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
									z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
									RotateEntity(de\OBJ, EntityPitch(de\OBJ, True), SecondPivotYaw + AngleDist(EntityYaw(de\OBJ, True), FirstPivotYaw), EntityRoll(de\OBJ, True), True)
								Else
									x = Max(Min((OBJPosX - SecondPivotX), Minus022), Plus022)
									z = Max(Min((OBJPosZ - SecondPivotZ), Minus022), Plus022)
								EndIf
								TeleportEntity(de\OBJ, FirstPivotX + x, FPSFactor01 + FirstPivotY + (OBJPosY - SecondPivotY), FirstPivotZ + z, -0.01, True)
								UpdateDecals()
							EndIf
						Next
						OpenCloseDoor(door1, (Not PlayerInsideMTElevator))
						door2\Open = False
						
						; ~ Return to default panel texture
						ClearElevatorPanelTexture(door1)
						ClearElevatorPanelTexture(door2)
						PlaySound2(ElevatorBeepSFX, Camera, SecondPivot, 4.0)
					EndIf
				EndIf
			EndIf
			For i = 0 To 1
				EntityTexture(door1\Buttons[i], d_I\ButtonTextureID[BUTTON_YELLOW_TEXTURE])
				EntityTexture(door2\Buttons[i], d_I\ButtonTextureID[BUTTON_YELLOW_TEXTURE])
			Next
		EndIf
	Else
		Local PrevEventState# = State
		
		If State < 0.0
			State = 0.0
			PrevEventState = 0.0
		EndIf
		
		If door1\OpenState = 0.0 And (Not door1\Open)
			If PlayerInsideMTElevator
				If State = 0.0
					TeleportEntity(n_I\Curr096\Collider, EntityX(door1\FrameOBJ), EntityY(door1\FrameOBJ) + 1.0, EntityZ(door1\FrameOBJ), n_I\Curr096\CollRadius)
					PointEntity(n_I\Curr096\Collider, FirstPivot)
					RotateEntity(n_I\Curr096\Collider, 0.0, EntityYaw(n_I\Curr096\Collider), 0.0)
					MoveEntity(n_I\Curr096\Collider, 0.0, 0.0, -0.5)
					ResetEntity(n_I\Curr096\Collider)
					n_I\Curr096\State = 6.0
					SetNPCFrame(n_I\Curr096, 0.0)
					LoadEventSound(event, "SFX\SCP\096\ElevatorSlam.ogg")
					State = State + (fps\Factor[0] * 1.4)
					door1\Locked = 1
					UpdateElevatorPanel(door1)
				EndIf
			EndIf
		EndIf
		
		If State > 0.0
			If PrevEventState = 0.0 Then event\SoundCHN = PlaySound_Strict(event\Sound, True)
			
			If State > 70.0 * 1.9 And State < (70.0 * 2.0) + fps\Factor[0]
				me\BigCameraShake = 7.0
			ElseIf State > 70.0 * 4.2 And State < (70.0 * 4.25) + fps\Factor[0]
				me\BigCameraShake = 2.0
			ElseIf State > 70.0 * 5.9 And State < (70.0 * 5.95) + fps\Factor[0]
				me\BigCameraShake = 2.0
			ElseIf State > 70.0 * 7.25 And State < (70.0 * 7.3) + fps\Factor[0]
				me\BigCameraShake = 2.0
				door1\FastOpen = True : door1\Open = True
				n_I\Curr096\State = 5.0
				n_I\Curr096\LastSeen = 1.0
			ElseIf State > 70.0 * 8.1 And State < 70.0 * 8.15 + fps\Factor[0]
				me\BigCameraShake = 2.0
			EndIf
			
			If State <= 70.0 * 8.1 Then door1\OpenState = Min(door1\OpenState, 20.0)
			State = State + fps\Factor[0]
		EndIf
	EndIf
	Return(State)
End Function

Type Decals
	Field OBJ%, Surf%, ID%
	Field Size#, SizeChange#, MaxSize#
	Field Alpha#, AlphaChange#
	Field BlendMode%, FX%
	Field R%, G%, B%
	Field Timer#, LifeTime#
	Field Dist#
End Type

Function CreateDecal.Decals(ID%, x#, y#, z#, Pitch#, Yaw#, Roll#, Size# = 1.0, Alpha# = 1.0, FX% = 0, BlendMode% = 1, R% = 0, G% = 0, B% = 0)
	Local de.Decals
	
	de.Decals = New Decals
	de\ID = ID
	de\Size = Size
	de\Alpha = Alpha
	de\FX = FX : de\BlendMode = BlendMode
	de\R = R : de\G = G : de\B = B
	de\MaxSize = 1.0
	
	de\OBJ = CreateMesh()
	de\Surf = CreateSurface(de\OBJ)
	
	Local v0% = AddVertex(de\Surf, -1.0, 1.0, 0.0, 0.0, 0.0)
	Local v1% = AddVertex(de\Surf, 1.0, 1.0, 0.0, 1.0, 0.0)
	Local v2% = AddVertex(de\Surf, 1.0, -1.0, 0.0, 1.0, 1.0)
	Local v3% = AddVertex(de\Surf, -1.0, -1.0, 0.0, 0.0, 1.0)
	
	AddTriangle(de\Surf, v0, v1, v2)
	AddTriangle(de\Surf, v0, v2, v3)
	
	PositionEntity(de\OBJ, x, y, z, True)
	ScaleEntity(de\OBJ, Size, Size, 1.0, True)
	RotateEntity(de\OBJ, Pitch, Yaw, Roll, True)
	EntityTexture(de\OBJ, de_I\DecalTextureID[ID])
	EntityAlpha(de\OBJ, Alpha)
	EntityFX(de\OBJ, FX)
	EntityBlend(de\OBJ, BlendMode)
	If R <> 0 Lor G <> 0 Lor B <> 0 Then EntityColor(de\OBJ, R, G, B)
	
	UpdateNormals(de\OBJ)
	HideEntity(de\OBJ)
	
	If de_I\DecalTextureID[ID] = 0 Then RuntimeError(Format(GetLocalString("runerr", "decals"), ID))
	
	Return(de)
End Function

Function RemoveDecal%(de.Decals)
	FreeEntity(de\OBJ) : de\OBJ = 0
	de\Surf = 0 
	Delete(de)
End Function

Function UpdateDecals%()
	Local de.Decals
	
	For de.Decals = Each Decals
		If EntityDistanceSquared(de\OBJ, me\Collider) < PowTwo(HideDistance)
			If EntityHidden(de\OBJ) Then ShowEntity(de\OBJ)
			
			Local DecalPosY# = EntityY(de\OBJ, True)
			
			If de\SizeChange <> 0.0
				de\Size = de\Size + (de\SizeChange * fps\Factor[0])
				ScaleEntity(de\OBJ, de\Size, de\Size, 1.0, True)
				
				Select(de\ID)
					Case 0
						;[Block]
						If de\Timer <= 0.0
							Local Angle# = Rnd(360.0)
							Local Temp# = Rnd(de\Size)
							Local de2.Decals
							
							de2.Decals = CreateDecal(DECAL_CORROSIVE_2, EntityX(de\OBJ, True) + Cos(Angle) * Temp, DecalPosY - 0.0005, EntityZ(de\OBJ, True) + Sin(Angle) * Temp, EntityPitch(de\OBJ, True), EntityYaw(de\OBJ, True), EntityRoll(de\OBJ, True), Rnd(0.1, 0.5))
							EntityParent(de2\OBJ, GetParent(de\OBJ))
							PlaySound2(DecaySFX[Rand(3)], Camera, de2\OBJ, 10.0, Rnd(0.1, 0.5))
							de\Timer = Rnd(50.0, 100.0)
						Else
							de\Timer = de\Timer - fps\Factor[0]
						EndIf
						;[End Block]
				End Select
				
				If de\Size >= de\MaxSize
					de\SizeChange = 0.0
					de\Size = de\MaxSize
				EndIf
			EndIf
			
			If de\AlphaChange <> 0.0
				de\Alpha = Min(de\Alpha + (fps\Factor[0] * de\AlphaChange), 1.0)
				EntityAlpha(de\OBJ, de\Alpha)
			EndIf
			If de\LifeTime > 0.0 Then de\LifeTime = Max(de\LifeTime - fps\Factor[0], 5.0)
			
			Local Dist# = DistanceSquared(EntityX(me\Collider), EntityX(de\OBJ, True), EntityZ(me\Collider), EntityZ(de\OBJ, True))
			Local ActualSize# = PowTwo(de\Size * 0.8)
			
			If (Dist < ActualSize) And (Int(EntityPitch(de\OBJ, True)) = 90.0) And (Abs((EntityY(me\Collider) - 0.3) - DecalPosY) < 0.05)
				Select(de\ID)
					Case 0
						;[Block]
						If de\FX <> 1
							DecalStep = 1
							me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, Max(100.0 - (Sqr(ActualSize - Dist)) * 15.0, 1.0))
							me\CrouchState = Max(me\CrouchState, (ActualSize - Dist) / 2.0)
						EndIf
						;[End Block]
					Case 2, 3, 4, 5, 6, 7, 16, 17, 18, 20
						;[Block]
						DecalStep = 2
						;[End Block]
				End Select
			EndIf
			
			If de\Size <= 0.0 Lor de\Alpha <= 0.0 Lor de\LifeTime = 5.0 Then RemoveDecal(de)
		Else
			If (Not EntityHidden(de\OBJ)) Then HideEntity(de\OBJ)
		EndIf
	Next
End Function

Type SecurityCams
	Field BaseOBJ%, CameraOBJ%, MonitorOBJ%, Pvt%
	Field ScrOBJ%
	Field Screen%, Cam%, ScrOverlay%
	Field Angle#, Turn#, CurrAngle#
	Field State#, PlayerState%
	Field SoundCHN%
	Field InSight% = False
	Field RenderInterval#
	Field room.Rooms
	Field FollowPlayer%
	Field CoffinEffect%
	Field AllowSaving%
	Field Dir%
	Field ScriptedMonitor% = False
	Field ScriptedCamera% = False
End Type

Function CreateSecurityCam.SecurityCams(room.Rooms, x1#, y1#, z1#, Pitch1#, Screen% = False, x2# = 0.0, y2# = 0.0, z2# = 0.0, Pitch2# = 0.0, Yaw2# = 0.0, Roll2# = 0.0)
	Local sc.SecurityCams
	
	sc.SecurityCams = New SecurityCams
	sc\room = room
	sc\ScriptedCamera = False
	sc\ScriptedMonitor = False
	
	sc\BaseOBJ = CopyEntity(sc_I\CamModelID[CAM_BASE_MODEL])
	ScaleEntity(sc\BaseOBJ, 0.0015, 0.0015, 0.0015)
	PositionEntity(sc\BaseOBJ, x1, y1, z1)
	If room <> Null Then EntityParent(sc\BaseOBJ, room\OBJ)
	
	sc\CameraOBJ = CopyEntity(sc_I\CamModelID[CAM_HEAD_MODEL])
	ScaleEntity(sc\CameraOBJ, 0.01, 0.01, 0.01)
	RotateEntity(sc\CameraOBJ, Pitch1, 0.0, 0.0)
	
	sc\Screen = Screen
	If Screen
		sc\AllowSaving = True
		
		sc\RenderInterval = opt\SecurityCamRenderIntervalLevel
		
		Local Scale# = RoomScale * 4.5 * 0.4
		
		sc\ScrOBJ = CreateSprite()
		ScaleSprite(sc\ScrOBJ, MeshWidth(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL]) * Scale * 0.95 * 0.5, MeshHeight(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL]) * Scale * 0.95 * 0.5)
		PositionEntity(sc\ScrOBJ, x2, y2, z2)
		RotateEntity(sc\ScrOBJ, Pitch2, Yaw2, Roll2)
		EntityFX(sc\ScrOBJ, 17)
		SpriteViewMode(sc\ScrOBJ, 2)
		EntityTexture(sc\ScrOBJ, sc_I\ScreenTex)
		If room <> Null Then EntityParent(sc\ScrOBJ, room\OBJ)
		HideEntity(sc\ScrOBJ)
		
		sc\ScrOverlay = CreateSprite(sc\ScrOBJ)
		ScaleSprite(sc\ScrOverlay, MeshWidth(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL]) * Scale * 0.95 * 0.5, MeshHeight(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL]) * Scale * 0.95 * 0.5)
		MoveEntity(sc\ScrOverlay, 0.0, 0.0, -0.005)
		EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY])
		SpriteViewMode(sc\ScrOverlay, 2)
		EntityFX(sc\ScrOverlay, 1)
		EntityBlend(sc\ScrOverlay, 3)
		HideEntity(sc\ScrOverlay)
		
		sc\MonitorOBJ = CopyEntity(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL], sc\ScrOBJ)
		ScaleEntity(sc\MonitorOBJ, Scale, Scale, Scale)
		
		sc\Cam = CreateCamera()
		CameraViewport(sc\Cam, 0, 0, 512, 512)
		CameraRange(sc\Cam, 0.05, 8.0)
		CameraZoom(sc\Cam, 0.8)
		HideEntity(sc\Cam)
	EndIf
	
	Return(sc)
End Function

Function TurnOffSecurityCam%(room.Rooms, TurnOff%)
	Local sc.SecurityCams
	
	For sc.SecurityCams = Each SecurityCams
		If sc\room = room
			If TurnOff
				If sc\Screen
					If sc\CoffinEffect <> 1 Then sc\CoffinEffect = 0
					HideEntity(sc\ScrOverlay)
					HideEntity(sc\ScrOBJ)
					sc\Screen = False
				EndIf
			Else
				If (Not sc\Screen)
					If sc\CoffinEffect = 0 Then sc\CoffinEffect = 2
					ShowEntity(sc\ScrOverlay)
					ShowEntity(sc\ScrOBJ)
					sc\Screen = True
				EndIf
			EndIf
			Exit
		EndIf
	Next
End Function

Function UpdateSecurityCams%()
	CatchErrors("UpdateSecurityCams()")
	
	Local sc.SecurityCams
	
	; ~ CoffinEffect = 0, not affected by SCP-895
	; ~ CoffinEffect = 1, constantly affected by SCP-895
	; ~ CoffinEffect = 2, SCP-079 can broadcast SCP-895 feed on this screen
	; ~ CoffinEffect = 3, SCP-079 broadcasting SCP-895 feed
	
	For sc.SecurityCams = Each SecurityCams
		Local Close% = (sc\room\Dist < 6.0 Lor PlayerRoom = sc\room)
		
		If Close Lor sc = sc_I\CoffinCam
			If sc\FollowPlayer
				If sc\Pvt = 0
					sc\Pvt = CreatePivot(sc\BaseOBJ)
					EntityParent(sc\Pvt, 0) ; ~ Sets position and rotation of the pivot to the cam object
				EndIf
				If EntityVisible(sc\CameraOBJ, Camera)
					If sc <> sc_I\CoffinCam
						MTFCameraCheckDetected = (MTFCameraCheckTimer > 0.0)
					EndIf
					
					PointEntity(sc\Pvt, Camera)
					
					RotateEntity(sc\CameraOBJ, CurveAngle(EntityPitch(sc\Pvt), EntityPitch(sc\CameraOBJ), 75.0), CurveAngle(EntityYaw(sc\Pvt), EntityYaw(sc\CameraOBJ), 75.0), 0.0)
				EndIf
				PositionEntity(sc\CameraOBJ, EntityX(sc\BaseOBJ, True), EntityY(sc\BaseOBJ, True) - 0.083, EntityZ(sc\BaseOBJ, True))
			Else
				If sc\Turn > 0.0
					If (Not sc\Dir)
						sc\CurrAngle = sc\CurrAngle + (0.2 * fps\Factor[0])
						If sc\CurrAngle > sc\Turn * 1.3 Then sc\Dir = True
					Else
						sc\CurrAngle = sc\CurrAngle - (0.2 * fps\Factor[0])
						If sc\CurrAngle < (-sc\Turn) * 1.3 Then sc\Dir = False
					EndIf
				EndIf
				PositionEntity(sc\CameraOBJ, EntityX(sc\BaseOBJ, True), EntityY(sc\BaseOBJ, True) - 0.083, EntityZ(sc\BaseOBJ, True))
				RotateEntity(sc\CameraOBJ, EntityPitch(sc\CameraOBJ), sc\room\Angle + sc\Angle + Max(Min(sc\CurrAngle, sc\Turn), -sc\Turn), 0.0)
				
				If sc\Cam <> 0
					PositionEntity(sc\Cam, EntityX(sc\CameraOBJ, True), EntityY(sc\CameraOBJ, True), EntityZ(sc\CameraOBJ, True))
					RotateEntity(sc\Cam, EntityPitch(sc\CameraOBJ), EntityYaw(sc\CameraOBJ), 0.0)
					MoveEntity(sc\Cam, 0.0, 0.0, 0.1)
				EndIf
				
				If sc <> sc_I\CoffinCam
					If Abs(DeltaYaw(sc\CameraOBJ, Camera)) < 60.0 And EntityVisible(sc\CameraOBJ, Camera) Then MTFCameraCheckDetected = (MTFCameraCheckTimer > 0.0)
				EndIf
			EndIf
			If (MilliSec Mod 1350) < 800
				EntityTexture(sc\CameraOBJ, sc_I\CamTextureID[CAM_HEAD_DEFAULT_TEXTURE])
			Else
				EntityTexture(sc\CameraOBJ, sc_I\CamTextureID[CAM_HEAD_RED_LIGHT_TEXTURE])
			EndIf
		EndIf
		
		If Close
			If sc\Screen
				If me\Sanity < -800.0
					me\RestoreSanity = False
					me\Sanity = -1010.0
					msg\DeathMsg = GetLocalString("death", "895")
					If me\VomitTimer < -10.0 Then Kill()
				EndIf
				
				sc\InSight = False
				If EntityDistanceSquared(me\Collider, sc\ScrOBJ) < PowTwo(opt\CameraFogFar * LightVolume * 1.2)
					sc\InSight = (EntityInView(sc\MonitorOBJ, Camera) And EntityVisible(Camera, sc\ScrOBJ))
					
					If me\BlinkTimer > -10.0 And sc\InSight
						Local Temp% = False
						Local RID% = sc\room\RoomTemplate\RoomID
						
						If RID = r_cont1_205 Lor RID = r_cont1_173_intro Then sc\CoffinEffect = 0 : Temp = True
						
						If sc\State < sc\RenderInterval
							sc\State = sc\State + fps\Factor[0]
						Else
							If sc_I\CoffinCam = Null Lor Rand(5) = 5 Lor sc\CoffinEffect <> 3
								UpdateLights(sc\Cam)
							Else
								UpdateLights(sc_I\CoffinCam\Cam)
							EndIf
							sc\State = 0.0
						EndIf
						
						If sc\CoffinEffect = 1 Lor sc\CoffinEffect = 3
							If I_714\Using <> 2 And wi\HazmatSuit <> 4 And wi\GasMask <> 4
								me\Sanity = me\Sanity - (fps\Factor[0] / (1.0 + I_714\Using))
								me\RestoreSanity = False
								If SelectedDifficulty\SaveType = SAVE_ON_SCREENS Then CanSave = 0
								
								Local Pvt% = CreatePivot()
								
								PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
								PointEntity(Pvt, sc\ScrOBJ)
								
								RotateEntity(me\Collider, EntityPitch(me\Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(me\Collider), Min(Max(15000.0 / (-me\Sanity), 20.0), 200.0)), 0.0)
								
								TurnEntity(Pvt, 90.0, 0.0, 0.0)
								CameraPitch = CurveAngle(EntityPitch(Pvt), CameraPitch + 90.0, Min(Max(15000.0 / (-me\Sanity), 20.0), 200.0))
								CameraPitch = CameraPitch - 90.0
								
								FreeEntity(Pvt) : Pvt = 0
								If me\Sanity < -800.0
									If Rand(3) = 1 Then EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY])
									If Rand(6) < 5
										EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[Rand(MONITOR_895_OVERLAY_1, MONITOR_895_OVERLAY_6)])
										If sc\PlayerState = 1 Then PlaySound_Strict(HorrorSFX[1])
										sc\PlayerState = 2
										If (Not ChannelPlaying(sc\SoundCHN)) Then sc\SoundCHN = PlaySound_Strict(HorrorSFX[4])
										If sc\CoffinEffect = 3 And Rand(200) = 1 Then sc\CoffinEffect = 2 : sc\PlayerState = Rand(10000, 20000)
									EndIf
									me\BlurTimer = 1000.0
									If me\VomitTimer = 0.0 Then me\VomitTimer = 1.0
								ElseIf me\Sanity < -500.0
									If Rand(7) = 1 Then EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY])
									If Rand(50) = 1
										EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[Rand(MONITOR_895_OVERLAY_1, MONITOR_895_OVERLAY_6)])
										If sc\PlayerState = 0 Then PlaySound_Strict(HorrorSFX[0])
										sc\PlayerState = Max(sc\PlayerState, 1)
										If sc\CoffinEffect = 3 And Rand(100) = 1 Then sc\CoffinEffect = 2 : sc\PlayerState = Rand(10000, 20000)
									EndIf
								Else
									EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY])
								EndIf
							Else
								EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY])
							EndIf
						ElseIf (Not Temp)
							If sc\PlayerState = 0 Then sc\PlayerState = Rand(60000, 65000)
							If Rand(500) = 1 Then EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[Rand(MONITOR_079_OVERLAY_2, MONITOR_079_OVERLAY_7)])
							If (MilliSec Mod sc\PlayerState) >= Rand(600)
								EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY])
							Else
								If (Not ChannelPlaying(sc\SoundCHN))
									sc\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\079\Broadcast" + Rand(3) + ".ogg"))
									If sc\CoffinEffect = 2 Then sc\CoffinEffect = 3 : sc\PlayerState = 0
								EndIf
								EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[Rand(MONITOR_079_OVERLAY_2, MONITOR_079_OVERLAY_7)])
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
			If (Not sc\InSight) And (Not sc\ScriptedCamera) Then sc\SoundCHN = LoopSound2(CameraSFX, sc\SoundCHN, Camera, sc\CameraOBJ, 4.0)
		EndIf
		
		If sc <> Null
			CatchErrors("Uncaught: UpdateSecurityCameras(Room ID: " + RID + ")")
		Else
			CatchErrors("Uncaught: UpdateSecurityCameras(Screen doesn't exist anymore!)")
		EndIf
	Next
End Function

Function RenderSecurityCams%()
	CatchErrors("RenderSecurityCams()")
	
	Local sc.SecurityCams
	
	For sc.SecurityCams = Each SecurityCams
		Local Close% = (sc\room\Dist < 6.0 Lor PlayerRoom = sc\room)
		
		If Close
			If sc\Screen
				If me\BlinkTimer > -10.0 And EntityDistanceSquared(me\Collider, sc\ScrOBJ) < PowTwo(opt\CameraFogFar * LightVolume * 1.2) And sc\InSight
					If sc\room\RoomTemplate\RoomID <> r_cont1_205
						If EntityHidden(sc\ScrOBJ) Then ShowEntity(sc\ScrOBJ)
						If EntityHidden(sc\ScrOverlay) Then ShowEntity(sc\ScrOverlay)
					EndIf
					
					If sc\State >= sc\RenderInterval
						If sc_I\CoffinCam = Null Lor Rand(5) = 5 Lor sc\CoffinEffect <> 3
							If (Not EntityHidden(Camera))
								ShowEntity(sc\Cam)
								HideEntity(Camera)
							EndIf
							Cls()
							
							SetBuffer(BackBuffer())
							RenderWorld(RenderTween)
							CopyRect(0, 0, 512, 512, 0, 0, BackBuffer(), TextureBuffer(sc_I\ScreenTex))
							
							If (Not EntityHidden(sc\Cam))
								ShowEntity(Camera)
								HideEntity(sc\Cam)
							EndIf
						Else
							If (Not EntityHidden(Camera))
								HideEntity(Camera)
								ShowEntity(sc_I\CoffinCam\room\OBJ)
								EntityAlpha(GetChild(sc_I\CoffinCam\room\OBJ, 2), 1.0)
								ShowEntity(sc_I\CoffinCam\Cam)
							EndIf
							Cls()
							
							SetBuffer(BackBuffer())
							RenderWorld(RenderTween)
							CopyRect(0, 0, 512, 512, 0, 0, BackBuffer(), TextureBuffer(sc_I\ScreenTex))
							
							If (Not EntityHidden(sc_I\CoffinCam\room\OBJ))
								HideEntity(sc_I\CoffinCam\Cam)
								ShowEntity(Camera)
								HideEntity(sc_I\CoffinCam\room\OBJ)
							EndIf
						EndIf
					EndIf
				Else
					If (Not EntityHidden(sc\ScrOBJ)) Then HideEntity(sc\ScrOBJ)
					If (Not EntityHidden(sc\ScrOverlay)) Then HideEntity(sc\ScrOverlay)
				EndIf
			EndIf
		EndIf
		
		If sc <> Null
			CatchErrors("Uncaught: RenderSecurityCameras(Room ID: " + sc\room\RoomTemplate\RoomID + ")")
		Else
			CatchErrors("Uncaught: RenderSecurityCameras(Screen doesn't exist anymore!)")
		EndIf
	Next
	Cls()
End Function

Function RemoveSecurityCam%(sc.SecurityCams)
	If sc\Pvt <> 0 Then FreeEntity(sc\Pvt) : sc\Pvt = 0
	FreeEntity(sc\CameraOBJ) : sc\CameraOBJ = 0
	FreeEntity(sc\BaseOBJ) : sc\BaseOBJ = 0
	If sc\Screen
		FreeEntity(sc\MonitorOBJ) : sc\MonitorOBJ = 0
		FreeEntity(sc\ScrOverlay) : sc\ScrOverlay = 0
		FreeEntity(sc\ScrOBJ) : sc\ScrOBJ = 0
		FreeEntity(sc\Cam) : sc\Cam = 0
	EndIf
	Delete(sc)
End Function

Function UpdateMonitorSaving%()
	If SelectedDifficulty\SaveType <> SAVE_ON_SCREENS Lor InvOpen Lor I_294\Using Lor OtherOpen <> Null Lor d_I\SelectedDoor <> Null Lor SelectedScreen <> Null Lor me\Health = 0 Then Return
	
	Local sc.SecurityCams
	
	For sc.SecurityCams = Each SecurityCams
		If sc\AllowSaving And sc\Screen
			Local Close% = (sc\room\Dist < 6.0 Lor PlayerRoom = sc\room)
			
			If Close
				If sc\InSight And EntityDistanceSquared(sc\ScrOBJ, Camera) < 1.0 And GrabbedEntity = 0 And d_I\ClosestButton = 0
					DrawHandIcon = True
					If HitKeyUse Then sc_I\SelectedMonitor = sc
					
					If sc_I\SelectedMonitor = sc
						Local Pvt% = CreatePivot()
						
						PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
						PointEntity(Pvt, sc\ScrOBJ)
						RotateEntity(me\Collider, EntityPitch(me\Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(me\Collider), Min(Max(15000.0 / (-me\Sanity), 20.0), 200.0)), 0.0)
						TurnEntity(Pvt, 90.0, 0.0, 0.0)
						CameraPitch = CurveAngle(EntityPitch(Pvt), CameraPitch + 90.0, Min(Max(15000.0 / (-me\Sanity), 20.0), 200.0))
						CameraPitch = CameraPitch - 90.0
						FreeEntity(Pvt) : Pvt = 0
					EndIf
				Else
					If sc_I\SelectedMonitor = sc Then sc_I\SelectedMonitor = Null
				EndIf
			Else
				If sc_I\SelectedMonitor = sc Then sc_I\SelectedMonitor = Null
			EndIf
		EndIf
	Next
End Function

Function UpdateCheckpointMonitors%(LCZ% = True)
	Local i%, SF%, b%, t1%
	Local Entity%, Name$
	
	Entity = mon_I\MonitorModelID[MONITOR_CHECKPOINT_MODEL]
	
	For i = 2 To CountSurfaces(Entity)
		SF = GetSurface(Entity, i)
		b = GetSurfaceBrush(SF)
		If b <> 0
			t1 = GetBrushTexture(b, 0)
			If t1 <> 0
				Name = StripPath(TextureName(t1))
				If Lower(Name) <> "monitortexture.jpg"
					If LCZ
						If mon_I\MonitorTimer[0] < 50.0
							BrushTexture(b, mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_2_OVERLAY], 0, 0)
						Else
							BrushTexture(b, mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_3_OVERLAY], 0, 0)
						EndIf
					Else
						If mon_I\MonitorTimer[1] < 50.0
							BrushTexture(b, mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_2_OVERLAY], 0, 0)
						Else
							BrushTexture(b, mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_1_OVERLAY], 0, 0)
						EndIf
					EndIf
					PaintSurface(SF, b)
				EndIf
				If Name <> "" Then FreeTexture(t1) : t1 = 0
			EndIf
			FreeBrush(b) : b = 0
		EndIf
	Next
	mon_I\UpdateCheckpoint[(1 - LCZ)] = True
End Function

Function TurnCheckpointMonitorsOff%(LCZ% = True)
	Local i%, SF%, b%, t1%
	Local Entity%, Name$
	
	If mon_I\UpdateCheckpoint[(1 - LCZ)]
		Entity = mon_I\MonitorModelID[MONITOR_CHECKPOINT_MODEL]
		
		For i = 2 To CountSurfaces(Entity)
			SF = GetSurface(Entity, i)
			b = GetSurfaceBrush(SF)
			If b <> 0
				t1 = GetBrushTexture(b, 0)
				If t1 <> 0
					Name = StripPath(TextureName(t1))
					If Lower(Name) <> "monitortexture.jpg"
						BrushTexture(b, mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_4_OVERLAY], 0, 0)
						PaintSurface(SF, b)
					EndIf
					If Name <> "" Then FreeTexture(t1) : t1 = 0
				EndIf
				FreeBrush(b) : b = 0
			EndIf
		Next
		SF = 0
		Entity = 0
		mon_I\UpdateCheckpoint[(1 - LCZ)] = False
		mon_I\MonitorTimer[(1 - LCZ)] = 0.0
	EndIf
End Function

Function TimeCheckpointMonitors%()
	If mon_I\UpdateCheckpoint[0]
		If mon_I\MonitorTimer[0] < 100.0
			mon_I\MonitorTimer[0] = Min(mon_I\MonitorTimer[0] + fps\Factor[0], 100.0)
		Else
			mon_I\MonitorTimer[0] = 0.0
		EndIf
	EndIf
	If mon_I\UpdateCheckpoint[1]
		If mon_I\MonitorTimer[1] < 100.0
			mon_I\MonitorTimer[1] = Min(mon_I\MonitorTimer[1] + fps\Factor[0], 100.0)
		Else
			mon_I\MonitorTimer[1] = 0.0
		EndIf
	EndIf
End Function

Global SelectedScreen.Screens

Type Screens
	Field OBJ%
	Field ImgPath$
	Field Img%
	Field room.Rooms
End Type

Type TempScreens
	Field ImgPath$
	Field x#, y#, z#
	Field RoomTemplate.RoomTemplates
End Type

Function CreateScreen.Screens(room.Rooms, x#, y#, z#, ImgPath$)
	Local s.Screens
	
	s.Screens = New Screens
	s\OBJ = CreatePivot()
	EntityRadius(s\OBJ, 0.1)
	EntityPickMode(s\OBJ, 1)
	PositionEntity(s\OBJ, x, y, z)
	If room <> Null Then EntityParent(s\OBJ, room\OBJ)
	
	s\ImgPath = ImgPath
	s\room = room
	
	Return(s)
End Function

Function UpdateScreens%()
	If InvOpen Lor I_294\Using Lor OtherOpen <> Null Lor d_I\SelectedDoor <> Null Lor SelectedScreen <> Null Then Return
	
	Local s.Screens
	
	For s.Screens = Each Screens
		If s\room = PlayerRoom
			If EntityDistanceSquared(me\Collider, s\OBJ) < 1.44
				EntityPick(Camera, 1.2)
				If PickedEntity() = s\OBJ And s\ImgPath <> ""
					DrawHandIcon = True
					If HitKeyUse
						SelectedScreen = s
						s\Img = LoadImage_Strict("GFX\Map\Screens\" + s\ImgPath)
						s\Img = ScaleImage2(s\Img, MenuScale, MenuScale)
						PlaySound_Strict(ButtonSFX[0])
						HitKeyUse = False
					EndIf
				EndIf
			EndIf
		EndIf
	Next
End Function

Function RemoveScreen%(s.Screens)
	FreeEntity(s\OBJ) : s\OBJ = 0
	If s\Img <> 0 Then FreeImage(s\Img) : s\Img = 0
	Delete(s)
End Function

Type Levers
	Field OBJ%, BaseOBJ%
	Field room.Rooms
End Type

Function CreateLever.Levers(room.Rooms, x#, y#, z#, Rotation# = 0.0, TurnedOn% = False)
	Local lvr.Levers
	
	lvr.Levers = New Levers
	
	lvr\room = room
	
	lvr\BaseOBJ = CopyEntity(lvr_I\LeverModelID[LEVER_BASE_MODEL])
	ScaleEntity(lvr\BaseOBJ, 0.036, 0.036, 0.036)
	PositionEntity(lvr\BaseOBJ, x, y, z, True)
	EntityParent(lvr\BaseOBJ, room\OBJ)
	RotateEntity(lvr\BaseOBJ, 0.0, Rotation, 0.0)
	
	lvr\OBJ = CopyEntity(lvr_I\LeverModelID[LEVER_HANDLE_MODEL])
	ScaleEntity(lvr\OBJ, 0.036, 0.036, 0.036)
	PositionEntity(lvr\OBJ, x, y, z, True)
	EntityParent(lvr\OBJ, room\OBJ)
	RotateEntity(lvr\OBJ, 80.0 + (-160.0 * TurnedOn), Rotation - 180.0, 0.0)
	EntityRadius(lvr\OBJ, 0.1)
	EntityPickMode(lvr\OBJ, 1, False)
	
	Return(lvr)
End Function

Function UpdateLever%(OBJ%, Locked% = False, MaxValue = 80.0, MinValue# = -80.0)
	Local PrevValue#, RefValue#
	Local Dist# = EntityDistanceSquared(Camera, OBJ)
	
	If Dist < 4.0 
		If Dist <= 0.65 And (Not Locked) 
			If EntityVisible(OBJ, Camera) 
				EntityPick(Camera, 0.65)
				
				If PickedEntity() = OBJ
					DrawHandIcon = True
					If HitKeyUse Then GrabbedEntity = OBJ
				EndIf
				
				PrevValue = EntityPitch(OBJ)
				
				If (DownKeyUse Lor HitKeyUse)
					If GrabbedEntity <> 0
						If GrabbedEntity = OBJ
							DrawHandIcon = True
							RotateEntity(GrabbedEntity, Max(Min(EntityPitch(OBJ) + Max(Min(mo\Mouse_Y_Speed_1 * 8.0, 30.0), -30.0), MaxValue), MinValue), EntityYaw(OBJ), 0.0)
							DrawArrowIcon[0] = True
							DrawArrowIcon[2] = True
						EndIf
					EndIf
				EndIf
				
				RefValue = EntityPitch(OBJ, True)
				If RefValue > (MaxValue - 5.0)
					If PrevValue =< (MaxValue - 5.0) Then PlaySound2(LeverSFX, Camera, OBJ, 1.0)
				ElseIf RefValue < (MinValue + 5.0)
					If PrevValue => (MinValue + 5.0) Then PlaySound2(LeverSFX, Camera, OBJ, 1.0)	
				EndIf
			EndIf
		EndIf
		If (Not DownKeyUse) And (Not HitKeyUse) Then GrabbedEntity = 0
		If GrabbedEntity = 0 Lor Dist > 0.65
			If EntityPitch(OBJ, True) > ((MaxValue + MinValue) / 2.0)
				RotateEntity(OBJ, CurveValue(MaxValue, EntityPitch(OBJ), 10.0), EntityYaw(OBJ), 0.0)
			Else
				RotateEntity(OBJ, CurveValue(MinValue, EntityPitch(OBJ), 10.0), EntityYaw(OBJ), 0.0)
			EndIf
		EndIf
	EndIf
	
	RefValue = EntityPitch(OBJ, True)
	If RefValue > ((MaxValue + MinValue) / 2.0)
		Return(False)
	Else
		Return(True)
	EndIf
End Function

Function RemoveLever(lvr.Levers)
	FreeEntity(lvr\OBJ) : lvr\OBJ = 0
	FreeEntity(lvr\BaseOBJ) : lvr\BaseOBJ = 0
	Delete(lvr)
End Function

Function CreateRedLight%(x#, y#, z#)
	Local Sprite%
	
	Sprite = CreateSprite()
	PositionEntity(Sprite, x, y, z)
	ScaleSprite(Sprite, 0.015, 0.015)
	EntityTexture(Sprite, misc_I\LightSpriteID[LIGHT_SPRITE_RED])
	EntityBlend(Sprite, 3)
	
	Return(Sprite)
End Function

Function UpdateRedLight%(Light%, Value1#, Value2#)
	If (MilliSec Mod Value1) < Value2
		If EntityHidden(Light) Then ShowEntity(Light)
	Else
		If (Not EntityHidden(Light)) Then HideEntity(Light)
	EndIf
End Function

Function CreateCustomCenter%(room.Rooms, x#, z#)
	room\RoomCenter = CreatePivot()
	PositionEntity(room\RoomCenter, x, 0.0, z)
	EntityParent(room\RoomCenter, room\OBJ)
End Function

Include "Source Code\Rooms_Core.bb"

Function UpdateRender%()
	Local it.Items, n.NPCs
	
	UpdateLightsTimer = 0.0
	UpdateLightVolume()
	UpdateLights(Camera)
	UpdateDoors()
	UpdateDecals()
	UpdateRooms()
	For it.Items = Each Items
		it\DistTimer = 0.0
	Next
	UpdateItems()
	For n.NPCs = Each NPCs
		n\AnimTimer = 0.0
		n\DropSpeed = 0.0
	Next
	UpdateNPCs()
End Function

Function TeleportToRoom%(r.Rooms)
	Local it.Items
	
	PlayerRoom = r
	UpdateRender()
	PlayerInsideMTElevator = False
End Function

Function HideRoomsNoColl%(room.Rooms)
	Local i%
	Local p.Props, d.Doors, sc.SecurityCams, lvr.Levers
	
	If (Not EntityHidden(room\OBJ))
		For p.Props = Each Props
			If p\room = room Then HideEntity(p\OBJ)
		Next
		
		For d.Doors = Each Doors
			If d\room = room
				HideEntity(d\OBJ)
				If d\OBJ2 <> 0 Then HideEntity(d\OBJ2)
				For i = 0 To 1
					If d\Buttons[i] <> 0 Then HideEntity(d\Buttons[i])
					If d\ElevatorPanel[i] <> 0 Then HideEntity(d\ElevatorPanel[i])
				Next
				HideEntity(d\FrameOBJ)
			EndIf
		Next
		
		For sc.SecurityCams = Each SecurityCams
			If sc\room = room
				If sc\MonitorOBJ <> 0
					If (Not sc\ScriptedMonitor) Then HideEntity(sc\MonitorOBJ)
				EndIf
				If (Not sc\ScriptedCamera)
					HideEntity(sc\CameraOBJ)
					HideEntity(sc\BaseOBJ)
				EndIf
			EndIf
		Next
		
		For lvr.Levers = Each Levers
			If lvr\room = room
				HideEntity(lvr\OBJ)
				HideEntity(lvr\BaseOBJ)
			EndIf
		Next
		
		For i = 0 To MaxRoomObjects - 1
			If room\Objects[i] <> 0
				If (Not room\ScriptedObject[i]) Then HideEntity(room\Objects[i])
			Else
				Exit
			EndIf
		Next
		
		HideEntity(room\OBJ)
	EndIf
End Function

Function ShowRoomsNoColl%(room.Rooms)
	Local i%
	Local p.Props, d.Doors, sc.SecurityCams, lvr.Levers
	
	If EntityHidden(room\OBJ)
		For p.Props = Each Props
			If p\room = room Then ShowEntity(p\OBJ)
		Next
		
		For d.Doors = Each Doors
			If d\room = room
				ShowEntity(d\OBJ)
				If d\OBJ2 <> 0 Then ShowEntity(d\OBJ2)
				For i = 0 To 1
					If d\Buttons[i] <> 0 Then ShowEntity(d\Buttons[i])
					If d\ElevatorPanel[i] <> 0 Then ShowEntity(d\ElevatorPanel[i])
				Next
				ShowEntity(d\FrameOBJ)
			EndIf
		Next
		
		For sc.SecurityCams = Each SecurityCams
			If sc\room = room
				If sc\MonitorOBJ <> 0
					If (Not sc\ScriptedMonitor) Then ShowEntity(sc\MonitorOBJ)
				EndIf
				If (Not sc\ScriptedCamera)
					ShowEntity(sc\CameraOBJ)
					ShowEntity(sc\BaseOBJ)
				EndIf
			EndIf
		Next
		
		For lvr.Levers = Each Levers
			If lvr\room = room
				ShowEntity(lvr\OBJ)
				ShowEntity(lvr\BaseOBJ)
			EndIf
		Next
		
		For i = 0 To MaxRoomObjects - 1
			If room\Objects[i] <> 0
				If (Not room\ScriptedObject[i]) Then ShowEntity(room\Objects[i])
			Else
				Exit
			EndIf
		Next
		
		;If room\TriggerBoxAmount > 0
		;	For i = 0 To room\TriggerBoxAmount - 1
		;		If chs\DebugHUD <> 0
		;			EntityColor(room\TriggerBoxes[i]\OBJ, 255, 255, 0)
		;			EntityAlpha(room\TriggerBoxes[i]\OBJ, 0.2)
		;		Else
		;			EntityColor(room\TriggerBoxes[i]\OBJ, 255, 255, 255)
		;			EntityAlpha(room\TriggerBoxes[i]\OBJ, 0.0)
		;		EndIf
		;	Next
		;EndIf
		
		ShowEntity(room\OBJ)
	EndIf
End Function

Function HideRoomsColl%(room.Rooms)
	Local i%, j%, k%
	Local p.Props, d.Doors, sc.SecurityCams, lvr.Levers
	
	If (Not room\HiddenAlpha)
		For p.Props = Each Props
			If p\room = room Then EntityAlpha(p\OBJ, 0.0)
		Next
		
		For d.Doors = Each Doors
			If d\room = room
				; ~ What the fuck is this? I really "like" how the adjacent door system works. Fuck this shit, I'm out -- Jabka
				Local Hide% = True
				
				For i = 0 To MaxRoomAdjacents - 1
					If PlayerRoom\AdjDoor[i] <> Null
						If d = PlayerRoom\AdjDoor[i] Then Hide = False
					EndIf
					If PlayerRoom\Adjacent[i] <> Null
						For j = 0 To MaxRoomAdjacents - 1
							If PlayerRoom\Adjacent[i]\AdjDoor[j] <> Null
								If d = PlayerRoom\Adjacent[i]\AdjDoor[j] Then Hide = False
							EndIf
							If PlayerRoom\Adjacent[i]\Adjacent[j] <> Null
								For k = 0 To MaxRoomAdjacents - 1 
									If PlayerRoom\Adjacent[i]\Adjacent[j]\AdjDoor[k] <> Null
										If d = PlayerRoom\Adjacent[i]\Adjacent[j]\AdjDoor[k] Then Hide = False
									EndIf
								Next
							EndIf
						Next
					EndIf
				Next
				If Hide
					EntityAlpha(d\OBJ, 0.0)
					If d\OBJ2 <> 0 Then EntityAlpha(d\OBJ2, 0.0)
					For i = 0 To 1
						If d\Buttons[i] <> 0 And d\DoorType <> WOODEN_DOOR And d\DoorType <> OFFICE_DOOR Then EntityAlpha(d\Buttons[i], 0.0)
						; ~ Hide collider anyway because player's collider cannot interact with it
						If d\ElevatorPanel[i] <> 0 Then HideEntity(d\ElevatorPanel[i])
					Next
					EntityAlpha(d\FrameOBJ, 0.0)
				EndIf
			EndIf
		Next
		
		; ~ Hide collider anyway because the player/NPC cannot interact with it
		For sc.SecurityCams = Each SecurityCams
			If sc\room = room
				If sc\MonitorOBJ <> 0
					If (Not sc\ScriptedMonitor) Then HideEntity(sc\MonitorOBJ)
				EndIf
				If (Not sc\ScriptedCamera)
					HideEntity(sc\CameraOBJ)
					HideEntity(sc\BaseOBJ)
				EndIf
			EndIf
		Next
		
		; ~ Hide collider anyway because the player/NPC cannot interact with it
		For lvr.Levers = Each Levers
			If lvr\room = room
				HideEntity(lvr\OBJ)
				HideEntity(lvr\BaseOBJ)
			EndIf
		Next
		
		; ~ Hide collider anyway because the player/NPC cannot interact with it
		For i = 0 To MaxRoomObjects - 1
			If room\Objects[i] <> 0
				If (Not room\ScriptedObject[i]) Then HideEntity(room\Objects[i])
			Else
				Exit
			EndIf
		Next
		
		EntityAlpha(GetChild(room\OBJ, 2), 0.0)
		room\HiddenAlpha = True
	EndIf
End Function

Function ShowRoomsColl%(room.Rooms)
	Local i%, j%, k%
	Local p.Props, d.Doors, sc.SecurityCams, lvr.Levers
	
	If room\HiddenAlpha
		For p.Props = Each Props
			If p\room = room Then EntityAlpha(p\OBJ, 1.0)
		Next
		
		For d.Doors = Each Doors
			If d\room = room
				Local Hide% = True
				
				For i = 0 To MaxRoomAdjacents - 1
					If PlayerRoom\AdjDoor[i] <> Null
						If d = PlayerRoom\AdjDoor[i] Then Hide = False
					EndIf
					If PlayerRoom\Adjacent[i] <> Null
						For j = 0 To MaxRoomAdjacents - 1
							If PlayerRoom\Adjacent[i]\AdjDoor[j] <> Null
								If d = PlayerRoom\Adjacent[i]\AdjDoor[j] Then Hide = False
							EndIf
							If PlayerRoom\Adjacent[i]\Adjacent[j] <> Null
								For k = 0 To MaxRoomAdjacents - 1 
									If PlayerRoom\Adjacent[i]\Adjacent[j]\AdjDoor[k] <> Null
										If d = PlayerRoom\Adjacent[i]\Adjacent[j]\AdjDoor[k] Then Hide = False
									EndIf
								Next
							EndIf
						Next
					EndIf
				Next
				If Hide
					EntityAlpha(d\OBJ, 1.0)
					If d\OBJ2 <> 0 And d\DoorType <> WOODEN_DOOR And d\DoorType <> OFFICE_DOOR Then EntityAlpha(d\OBJ2, 1.0)
					For i = 0 To 1
						If d\Buttons[i] <> 0 And d\DoorType <> WOODEN_DOOR And d\DoorType <> OFFICE_DOOR Then EntityAlpha(d\Buttons[i], 1.0)
						If d\ElevatorPanel[i] <> 0 Then ShowEntity(d\ElevatorPanel[i])
					Next
					EntityAlpha(d\FrameOBJ, 1.0)
				EndIf
			EndIf
		Next
		
		For sc.SecurityCams = Each SecurityCams
			If sc\room = room
				If sc\MonitorOBJ <> 0
					If (Not sc\ScriptedMonitor) Then ShowEntity(sc\MonitorOBJ)
				EndIf
				If (Not sc\ScriptedCamera)
					ShowEntity(sc\CameraOBJ)
					ShowEntity(sc\BaseOBJ)
				EndIf
			EndIf
		Next
		
		For lvr.Levers = Each Levers
			If lvr\room = room
				ShowEntity(lvr\OBJ)
				ShowEntity(lvr\BaseOBJ)
			EndIf
		Next
		
		For i = 0 To MaxRoomObjects - 1
			If room\Objects[i] <> 0
				If (Not room\ScriptedObject[i]) Then ShowEntity(room\Objects[i])
			Else
				Exit
			EndIf
		Next
		
		EntityAlpha(GetChild(room\OBJ, 2), 1.0)
		room\HiddenAlpha = False
	EndIf
End Function

Function UpdateRooms%()
	CatchErrors("UpdateRooms()")
	
	Local Dist#, i%, j%, r.Rooms
	Local x#, y#, z#, Hide%
	Local PlayerX# = EntityX(me\Collider, True)
	Local PlayerY# = EntityY(me\Collider, True)
	Local PlayerZ# = EntityZ(me\Collider, True)
	
	; ~ The reason why it is like this:
	; ~ When the map gets spawned by a seed, it starts from LCZ to HCZ to EZ (bottom to top)
	; ~ A map loaded by the map creator starts from EZ to HCZ to LCZ (top to bottom) and that's why this little code thing with the (SelectedCustomMap = Null) needs to be there - ENDSHN
;	If (PlayerZ / RoomSpacing) < I_Zone\Transition[1] - (SelectedCustomMap = Null)
;		me\Zone = 2
;	ElseIf (PlayerZ / RoomSpacing) >= I_Zone\Transition[1] - (SelectedCustomMap = Null) And (PlayerZ / RoomSpacing) < I_Zone\Transition[0] - (SelectedCustomMap = Null)
;		me\Zone = 1
;	Else
;		me\Zone = 0
;	EndIf
	; ~ TODO: Check If Needed - Wolfnaya
	Local FoundNewPlayerRoom% = False
	
	If Abs(PlayerY - EntityY(PlayerRoom\OBJ)) < 1.5
		x = Abs(PlayerRoom\x - PlayerX)
		If x < 4.0
			z = Abs(PlayerRoom\z - PlayerZ)
			If z < 4.0 Then FoundNewPlayerRoom = True
		EndIf
		
		If (Not FoundNewPlayerRoom) ; ~ It's likely that an adjacent room is the new player room, check for that
			For i = 0 To MaxRoomAdjacents - 1
				If PlayerRoom\Adjacent[i] <> Null
					x = Abs(PlayerRoom\Adjacent[i]\x - PlayerX)
					If x < 4.0
						z = Abs(PlayerRoom\Adjacent[i]\z - PlayerZ)
						If z < 4.0
							y = Abs(PlayerRoom\Adjacent[i]\y - PlayerY)
							If y < 4.0
								FoundNewPlayerRoom = True
								PlayerRoom = PlayerRoom\Adjacent[i]
								Exit
							EndIf
						EndIf
					EndIf
				EndIf
			Next
		EndIf
	EndIf
	
	For r.Rooms = Each Rooms
		x = Abs(r\x - PlayerX)
		z = Abs(r\z - PlayerZ)
		r\Dist = Max(x, z)
		
		If x < 16 And z < 16
			If (Not FoundNewPlayerRoom) And PlayerRoom <> r
				If x < 4.0
					If z < 4.0
						If Abs(PlayerY - EntityY(r\OBJ)) < 1.5 Then PlayerRoom = r
						FoundNewPlayerRoom = True
					EndIf
				EndIf
			EndIf
		EndIf
		
		Hide = True
		If r = PlayerRoom Then Hide = False
		If IsRoomAdjacent(PlayerRoom, r) Then Hide = False
		For i = 0 To MaxRoomAdjacents - 1
			If IsRoomAdjacent(PlayerRoom\Adjacent[i], r)
				Hide = False
				Exit
			EndIf
		Next
		
		If Hide
			HideRoomsNoColl(r)
		Else
			ShowRoomsNoColl(r)
		EndIf
	Next
	
	CurrMapGrid\Found[Floor(EntityX(PlayerRoom\OBJ) / RoomSpacing) + (Floor(EntityZ(PlayerRoom\OBJ) / RoomSpacing) * MapGridSize)] = MapGrid_Tile
	PlayerRoom\Found = True
	
	ShowRoomsColl(PlayerRoom)
	For i = 0 To MaxRoomAdjacents - 1
		If PlayerRoom\Adjacent[i] <> Null
			If PlayerRoom\AdjDoor[i] <> Null
				If PlayerRoom\Adjacent[i] <> PlayerRoom
					If PlayerY > 8.0 Lor PlayerY < -8.0
						HideRoomsColl(PlayerRoom\Adjacent[i])
					Else
						If PlayerRoom\AdjDoor[i]\OpenState = 0.0 Lor (Not EntityInView(PlayerRoom\AdjDoor[i]\FrameOBJ, Camera))
							HideRoomsColl(PlayerRoom\Adjacent[i])
						Else
							ShowRoomsColl(PlayerRoom\Adjacent[i])
						EndIf
					EndIf
				EndIf
			EndIf
			
			For j = 0 To MaxRoomAdjacents - 1
				If PlayerRoom\Adjacent[i]\Adjacent[j] <> Null
					If PlayerRoom\Adjacent[i]\Adjacent[j] <> PlayerRoom Then HideRoomsColl(PlayerRoom\Adjacent[i]\Adjacent[j])
				EndIf
			Next
		EndIf
	Next
	
	CatchErrors("Uncaught: UpdateRooms()")
End Function

Function IsRoomAdjacent%(this.Rooms, that.Rooms)
	If this = Null Lor that = Null Then Return(False)
	If this = that Then Return(True)
	
	Local i%
	
	For i = 0 To MaxRoomAdjacents - 1
		If that = this\Adjacent[i] Then Return(True)
	Next
	Return(False)
End Function

Dim MapRoom$(0, 0)

Function SetRoom%(RoomName$, RoomType%, RoomPosition%, MinPos%, MaxPos%) ; ~ Place a room without overwriting others
	Local Looped%, CanPlace%
	
	Looped = False
	CanPlace = True
	While MapRoom(RoomType, RoomPosition) <> ""
		RoomPosition = RoomPosition + 1
		If RoomPosition > MaxPos
			If (Not Looped)
				RoomPosition = MinPos + 1 : Looped = True
			Else
				CanPlace = False
				Exit
			EndIf
		EndIf
	Wend
	If CanPlace
		MapRoom(RoomType, RoomPosition) = RoomName
		Return(True)
	Else
		Return(False)
	EndIf
End Function

Function PreventRoomOverlap%(r.Rooms)
	If r\RoomTemplate\DisableOverlapCheck Then Return
	
	Local r2.Rooms, r3.Rooms
	Local IsIntersecting% = False
	Local RID% = r\RoomTemplate\RoomID
	
	; ~ Just skip it when it would try to check for the checkpoints
	If RID = r_room2_checkpoint_lcz_hcz Lor RID = r_room2_checkpoint_hcz_ez Lor RID = r_cont1_173 Then Return(True)
	
	; ~ First, check if the room is actually intersecting at all
	For r2.Rooms = Each Rooms
		If r2 <> r And (Not r2\RoomTemplate\DisableOverlapCheck)
			If CheckRoomOverlap(r, r2)
				IsIntersecting = True
				Exit
			EndIf
		EndIf
	Next
	
	; ~ If not, then simply return it as True
	If (Not IsIntersecting) Then Return(True)
	
	; ~ Room is interseting: First, check if the given room is a ROOM2, so we could potentially just turn it by 180.0 degrees
	IsIntersecting = False
	
	Local x% = r\x / RoomSpacing
	Local y% = r\z / RoomSpacing
	
	If r\RoomTemplate\Shape = ROOM2
		; ~ Room is a ROOM2, let's check if turning it 180.0 degrees fixes the overlapping issue
		r\Angle = r\Angle + 180.0
		RotateEntity(r\OBJ, 0.0, r\Angle, 0.0)
		CalculateRoomExtents(r)
		
		For r2.Rooms = Each Rooms
			If r2 <> r And (Not r2\RoomTemplate\DisableOverlapCheck)
				If CheckRoomOverlap(r, r2)
					; ~ If didn't work then rotate the room back and move to the next step
					IsIntersecting = True
					r\Angle = r\Angle - 180.0
					RotateEntity(r\OBJ, 0.0, r\Angle, 0.0)
					CalculateRoomExtents(r)
					Exit
				EndIf
			EndIf
		Next
	Else
		IsIntersecting = True
	EndIf
	
	; ~ Room is ROOM2 and was able to be turned by 180.0 degrees
	If (Not IsIntersecting) Then Return(True)
	
	; ~ Room is either not a ROOM2 or the ROOM2 is still intersecting, now trying to swap the room with another of the same type
	IsIntersecting = True
	
	Local x2%, y2%, Rot%, Rot2%
	
	For r2.Rooms = Each Rooms
		If r2 <> r And (Not r2\RoomTemplate\DisableOverlapCheck)
			RID = r2\RoomTemplate\RoomID
			
			If r\RoomTemplate\Shape = r2\RoomTemplate\Shape And r\Zone = r2\Zone And (RID <> r_room2_checkpoint_lcz_hcz And RID <> r_room2_checkpoint_hcz_ez And RID <> r_cont1_173)
				x = r\x / RoomSpacing
				y = r\z / RoomSpacing
				Rot = r\Angle
				
				x2 = r2\x / RoomSpacing
				y2 = r2\z / RoomSpacing
				Rot2 = r2\Angle
				
				IsIntersecting = False
				
				r\x = x2 * 8.0
				r\z = y2 * 8.0
				r\Angle = Rot2
				PositionEntity(r\OBJ, r\x, r\y, r\z)
				RotateEntity(r\OBJ, 0.0, r\Angle, 0.0)
				CalculateRoomExtents(r)
				
				r2\x = x * 8.0
				r2\z = y * 8.0
				r2\Angle = Rot
				PositionEntity(r2\OBJ, r2\x, r2\y, r2\z)
				RotateEntity(r2\OBJ, 0.0, r2\Angle, 0.0)
				CalculateRoomExtents(r2)
				
				; ~ Make sure neither room overlaps with anything after the swap
				For r3.Rooms = Each Rooms
					If (Not r3\RoomTemplate\DisableOverlapCheck)
						If r3 <> r
							If CheckRoomOverlap(r, r3)
								IsIntersecting = True
								Exit
							EndIf
						EndIf
						If r3 <> r2
							If CheckRoomOverlap(r2, r3)
								IsIntersecting = True
								Exit
							EndIf
						EndIf
					EndIf
				Next
				
				; ~ Either the original room or the "reposition" room is intersecting, reset the position of each room to their original one
				If IsIntersecting
					r\x = x * 8.0
					r\z = y * 8.0
					r\Angle = Rot
					PositionEntity(r\OBJ, r\x, r\y, r\z)
					RotateEntity(r\OBJ, 0.0, r\Angle, 0.0)
					CalculateRoomExtents(r)
					
					r2\x = x2 * 8.0
					r2\z = y2 * 8.0
					r2\Angle = Rot2
					PositionEntity(r2\OBJ, r2\x, r2\y, r2\z)
					RotateEntity(r2\OBJ, 0.0, r2\Angle, 0.0)
					CalculateRoomExtents(r2)
					
					IsIntersecting = False
				EndIf
			EndIf
		EndIf
	Next
	
	; ~ Room was able to the placed in a different spot
	If (Not IsIntersecting) Then Return(True)
	
	Return(False)
End Function

Const MapGridSize% = 18
Const RoomSpacing# = 8.0

Type MapGrid
	Field Grid%[PowTwo(MapGridSize + 1)]
	Field Angle%[PowTwo(MapGridSize + 1)]
	Field Found%[PowTwo(MapGridSize + 1)]
	Field RoomName$[PowTwo(MapGridSize)]
	Field RoomID%[ROOM4 + 1]
End Type

Global CurrMapGrid.MapGrid

; ~ Map Grid Tile ID Constants
;[Block]
Const MapGrid_NoTile% = 0
Const MapGrid_Tile% = 1
Const MapGrid_CheckpointTile% = 255
;[End Block]

Type MapZones
	Field Transition%[2]
	Field HasCustomForest%
	Field HasCustomMT%
End Type

Global I_Zone.MapZones

Function CreateMap%()
	Local r.Rooms, r2.Rooms, d.Doors
	Local x%, y%, Temp%, Temp2%
	Local i%, x2%, y2%
	Local Width%, Height%, TempHeight%, yHallways%
	Local ShouldSpawnDoor%, Zone%
	Local RoomID%, DoorID%
	
	I_Zone\Transition[0] = Floor(MapGridSize * (2.0 / 3.0)) + 1
	I_Zone\Transition[1] = Floor(MapGridSize * (1.0 / 3.0)) + 1
	I_Zone\HasCustomForest = False
	I_Zone\HasCustomMT = False
	
	SeedRnd(GenerateSeedNumber(RandomSeed))
	
	Delete(CurrMapGrid)
	CurrMapGrid = New MapGrid
	
	x = MapGridSize / 2
	y = MapGridSize - 2
	
	For i = y To MapGridSize - 1
		CurrMapGrid\Grid[x + (i * MapGridSize)] = MapGrid_Tile
	Next
	
;	If szl\CurrentZone <> SURFACE ; ~ Generate rooms only if they're inside the facility
	Repeat
		Width = Rand(Floor(MapGridSize * 0.6), Floor(MapGridSize * 0.85))
		
		If x > MapGridSize * 0.6
			Width = -Width
		ElseIf x > MapGridSize * 0.4
			x = x - (Width / 2)
		EndIf
		
		; ~ Make sure the hallway doesn't go outside the array
		If x + Width > MapGridSize - 3
			Width = MapGridSize - 3 - x
		ElseIf x + Width < 2
			Width = (-x) + 2
		EndIf
		
		x = Min(x, x + Width)
		Width = Abs(Width)
		For i = x To x + Width
			CurrMapGrid\Grid[Min(i, MapGridSize) + (y * MapGridSize)] = MapGrid_Tile
		Next
		
		Height = Rand(3, 4)
		If y - Height < 1 Then Height = y - 1
		
		yHallways = Rand(4, 5)
		
		If GetZone(y - Height) <> GetZone(y - Height + 1) Then Height = Height - 1
		
		For i = 1 To yHallways
			x2 = Max(Min(Rand(x, x + Width - 1), MapGridSize - 2), 2.0)
			While CurrMapGrid\Grid[x2 + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x2 - 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x2 + 1) + ((y - 1) * MapGridSize)]
				x2 = x2 + 1
			Wend
			
			If x2 < x + Width
				If i = 1
					TempHeight = Height
					If Rand(2) = 1
						x2 = x
					Else
						x2 = x + Width
					EndIf
				Else
					TempHeight = Rand(Height)
				EndIf
				
				For y2 = y - TempHeight To y
					If GetZone(y2) <> GetZone(y2 + 1) ; ~ A room leading from zone to another
						CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] = MapGrid_CheckpointTile
					Else
						CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] = MapGrid_Tile
					EndIf
				Next
				If TempHeight = Height Then Temp = x2
			EndIf
		Next
		x = Temp
		y = y - Height
	Until y < 2
	
	Local Room1Amount%[ZONEAMOUNT], Room2Amount%[ZONEAMOUNT], Room2CAmount%[ZONEAMOUNT], Room3Amount%[ZONEAMOUNT], Room4Amount%[ZONEAMOUNT]
	
	; ~ Count the amount of rooms
	For y = 1 To MapGridSize - 1
		Zone = GetZone(y)
		For x = 1 To MapGridSize - 1
			If CurrMapGrid\Grid[x + (y * MapGridSize)] > MapGrid_NoTile
				Temp = 0
				Temp = Min(CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)], 1.0)
				If CurrMapGrid\Grid[x + (y * MapGridSize)] <> MapGrid_CheckpointTile Then CurrMapGrid\Grid[x + (y * MapGridSize)] = Temp
				Select(CurrMapGrid\Grid[x + (y * MapGridSize)])
					Case 1
						;[Block]
						Room1Amount[Zone] = Room1Amount[Zone] + 1
						;[End Block]
					Case 2
						;[Block]
						If Min(CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)], 1.0) = 2
							Room2Amount[Zone] = Room2Amount[Zone] + 1
						ElseIf Min(CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)], 1.0) = 2
							Room2Amount[Zone] = Room2Amount[Zone] + 1
						Else
							Room2CAmount[Zone] = Room2CAmount[Zone] + 1
						EndIf
						;[End Block]
					Case 3
						;[Block]
						Room3Amount[Zone] = Room3Amount[Zone] + 1
						;[End Block]
					Case 4
						;[Block]
						Room4Amount[Zone] = Room4Amount[Zone] + 1
						;[End Block]
				End Select
			EndIf
		Next
	Next
	
	Local Placed%
	Local y_min%, y_max%, x_min%, x_max%
	
	; ~ Force more room1s (if needed)
	For i = 0 To 2
		; ~ Need more rooms if there are less than 5 of them
		Temp = (-Room1Amount[i]) + 5
		If Temp > 0
			If i = 2
				y_min = 1
			Else
				y_min = I_Zone\Transition[i]
			EndIf
			If i = 0
				y_max = MapGridSize - 2
			Else
				y_max = I_Zone\Transition[i - 1] - 1
			EndIf
			x_min = 1
			x_max = MapGridSize - 2
			
			For y = y_min To y_max
				For x = x_min To x_max
					If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_NoTile
						If (Min(CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)], 1.0)) = 1
							If CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)]
								x2 = x + 1 : y2 = y
							ElseIf CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)]
								x2 = x - 1 : y2 = y
							ElseIf CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)]
								x2 = x : y2 = y + 1
							ElseIf CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)]
								x2 = x : y2 = y - 1
							EndIf
							
							Placed = False
							If CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] > 1 And CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] < 4 And (y < y_max Lor y2 < y Lor i = 0)
								Select(CurrMapGrid\Grid[x2 + (y2 * MapGridSize)])
									Case 2
										;[Block]
										If Min(CurrMapGrid\Grid[(x2 + 1) + (y2 * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[(x2 - 1) + (y2 * MapGridSize)], 1.0) = 2
											Room2Amount[i] = Room2Amount[i] - 1
											Room3Amount[i] = Room3Amount[i] + 1
											Placed = True
										ElseIf Min(CurrMapGrid\Grid[x2 + ((y2 + 1) * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x2 + ((y2 - 1) * MapGridSize)], 1.0) = 2
											Room2Amount[i] = Room2Amount[i] - 1
											Room3Amount[i] = Room3Amount[i] + 1
											Placed = True
										EndIf
										;[End Block]
									Case 3
										;[Block]
										Room3Amount[i] = Room3Amount[i] - 1
										Room4Amount[i] = Room4Amount[i] + 1
										Placed = True
										;[End Block]
								End Select
								
								If Placed
									CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] = CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] + 1
									
									CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_Tile
									Room1Amount[i] = Room1Amount[i] + 1
									
									Temp = Temp - 1
								EndIf
							EndIf
						EndIf
					EndIf
					If Temp = 0 Then Exit
				Next
				If Temp = 0 Then Exit
			Next
		EndIf
	Next
	
	; ~ Force more ROOM4 and ROOM2C
	For i = 0 To 2
		If i = 2
			y_min = 2
		Else
			y_min = I_Zone\Transition[i]
		EndIf
		If i = 0
			y_max = MapGridSize - 2
		Else
			y_max = I_Zone\Transition[i - 1] - 2
		EndIf
		x_min = 1
		x_max = MapGridSize - 2
		
		If Room4Amount[i] < 1 ; ~ We want at least one ROOM4
			Temp = 0
			For y = y_min To y_max
				For x = x_min To x_max
					If CurrMapGrid\Grid[x + (y * MapGridSize)] = 3
						Select(False) ; ~ See if adding a ROOM1 is possible
							Case (CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] Lor CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x + 2) + (y * MapGridSize)] Lor x = x_max)
								;[Block]
								CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] = 1
								Temp = 1
								;[End Block]
							Case (CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] Lor CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x - 2) + (y * MapGridSize)] Lor x = x_min)
								;[Block]
								CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] = 1
								Temp = 1
								;[End Block]
							Case (CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[x + ((y + 2) * MapGridSize)] Lor (i = 0 And y = y_max))
								;[Block]
								CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] = 1
								Temp = 1
								;[End Block]
							Case (CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[x + ((y - 2) * MapGridSize)] Lor (i < 2 And y = y_min))
								;[Block]
								CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] = 1
								Temp = 1
								;[End Block]
						End Select
						If Temp = 1
							CurrMapGrid\Grid[x + (y * MapGridSize)] = 4 ; ~ Turn this room into a ROOM4
							Room4Amount[i] = Room4Amount[i] + 1
							Room3Amount[i] = Room3Amount[i] - 1
							Room1Amount[i] = Room1Amount[i] + 1
						EndIf
					EndIf
					If Temp = 1 Then Exit
				Next
				If Temp = 1 Then Exit
			Next
		EndIf
		
		If Room2CAmount[i] < 1 ; ~ We want at least one ROOM2C
			Temp = 0
			For y = y_max To y_min Step -1
				For x = x_min To x_max
					If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_Tile
						Select(True) ; ~ See if adding some rooms is possible
							Case CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] > MapGrid_NoTile
								;[Block]
								If (CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 2) + (y * MapGridSize)]) = 0 And x < x_max
									If (CurrMapGrid\Grid[(x + 1) + ((y - 2) * MapGridSize)] + CurrMapGrid\Grid[(x + 2) + ((y - 1) * MapGridSize)]) = 0 And (y > y_min Lor i = 2)
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] = 1
										Temp = 1
									ElseIf (CurrMapGrid\Grid[(x + 1) + ((y + 2) * MapGridSize)] + CurrMapGrid\Grid[(x + 2) + ((y + 1) * MapGridSize)]) = 0 And (y < y_max Lor i > 0)
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] = 1
										Temp = 1
									EndIf
								EndIf
								;[End Block]
							Case CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] > MapGrid_NoTile
								;[Block]
								If (CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 2) + (y * MapGridSize)]) = 0 And x > x_min
									If (CurrMapGrid\Grid[(x - 1) + ((y - 2) * MapGridSize)] + CurrMapGrid\Grid[(x - 2) + ((y - 1) * MapGridSize)]) = 0 And (y > y_min Lor i = 2)
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] = 1
										Temp = 1
									ElseIf (CurrMapGrid\Grid[(x - 1) + ((y + 2) * MapGridSize)] + CurrMapGrid\Grid[(x - 2) + ((y + 1) * MapGridSize)]) = 0 And (y < y_max Lor i > 0)
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] = 1
										Temp = 1
									EndIf
								EndIf
								;[End Block]
							Case CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] > MapGrid_NoTile
								;[Block]
								If (CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[x + ((y + 2) * MapGridSize)]) = 0 And (y < y_max Lor i > 0)
									If (CurrMapGrid\Grid[(x - 2) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 1) + ((y + 2) * MapGridSize)]) = 0 And x > x_min
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] = 1
										Temp = 1
									ElseIf (CurrMapGrid\Grid[(x + 2) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y + 2) * MapGridSize)]) = 0 And x < x_max
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] = 1
										Temp = 1
									EndIf
								EndIf
								;[End Block]
							Case CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile
								;[Block]
								If (CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[x + ((y - 2) * MapGridSize)]) = 0 And (y > y_min Lor i = 2)
									If (CurrMapGrid\Grid[(x - 2) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 1) + ((y - 2) * MapGridSize)]) = 0 And x > x_min
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] = 1
										Temp = 1
									ElseIf (CurrMapGrid\Grid[(x + 2) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y - 2) * MapGridSize)]) = 0 And x < x_max
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] = 1
										Temp = 1
									EndIf
								EndIf
								;[End Block]
						End Select
						If Temp = 1
							Room2CAmount[i] = Room2CAmount[i] + 1
							Room2Amount[i] = Room2Amount[i] + 1
						EndIf
					EndIf
					If Temp = 1 Then Exit
				Next
				If Temp = 1 Then Exit
			Next
		EndIf
	Next
	
	Local MaxRooms% = 55 * MapGridSize / 20
	
	MaxRooms = Max(MaxRooms, Room1Amount[0] + Room1Amount[1] + Room1Amount[2] + 1)
	MaxRooms = Max(MaxRooms, Room2Amount[0] + Room2Amount[1] + Room2Amount[2] + 1)
	MaxRooms = Max(MaxRooms, Room2CAmount[0] + Room2CAmount[1] + Room2CAmount[2] + 1)
	MaxRooms = Max(MaxRooms, Room3Amount[0] + Room3Amount[1] + Room3Amount[2] + 1)
	MaxRooms = Max(MaxRooms, Room4Amount[0] + Room4Amount[1] + Room4Amount[2] + 1)
	
	Dim MapRoom$(ROOM4 + 1, MaxRooms + 1)
	
	; ~ [LIGHT CONTAINMENT ZONE]
	
	Local MinPos% = 1, MaxPos% = Room1Amount[0] - 1
	
	MapRoom(ROOM1, 0) = "cont1_173"
	
	SetRoom("cont1_372", ROOM1, Floor(0.1 * Float(Room1Amount[0])), MinPos, MaxPos)
	SetRoom("cont1_005", ROOM1, Floor(0.3 * Float(Room1Amount[0])), MinPos, MaxPos)
	SetRoom("cont1_914", ROOM1, Floor(0.35 * Float(Room1Amount[0])), MinPos, MaxPos)
	SetRoom("cont1_205", ROOM1, Floor(0.5 * Float(Room1Amount[0])), MinPos, MaxPos)
	SetRoom("room1_archive", ROOM1, Floor(0.6 * Float(Room1Amount[0])), MinPos, MaxPos)
	
	MapRoom(ROOM2C, 0) = "room2c_gw_lcz"
	
	MinPos = 1
	MaxPos = Room2Amount[0] - 1
	
	MapRoom(ROOM2, 0) = "room2_closets" 
	
	SetRoom("room2_test_lcz", ROOM2, Floor(0.1 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("cont2_427_714_860_1025", ROOM2, Floor(0.2 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2_storage", ROOM2, Floor(0.3 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2_gw_2", ROOM2, Floor(0.4 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2_sl", ROOM2, Floor(0.5 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("cont2_012", ROOM2, Floor(0.55 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("cont2_500_1499", ROOM2, Floor(0.6 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("cont2_1123", ROOM2, Floor(0.75 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2_js", ROOM2, Floor(0.85 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2_elevator", ROOM2, Floor(0.9 * Float(Room2Amount[0])), MinPos, MaxPos)
	
	MapRoom(ROOM2C, Floor(0.5 * Float(Room2CAmount[0]))) = "cont2c_066_1162_arc"
	
	MapRoom(ROOM3, Floor(Rnd(0.2, 0.8) * Float(Room3Amount[0]))) = "room3_storage"
	
	MapRoom(ROOM4, Floor(0.3 * Float(Room4Amount[0]))) = "room4_ic"
	
	; ~ [HEAVY CONTAINMENT ZONE]
	
	MinPos = Room1Amount[0]
	MaxPos = Room1Amount[0] + Room1Amount[1] - 1
	
	SetRoom("cont1_079", ROOM1, Room1Amount[0] + Floor(0.15 * Float(Room1Amount[1])), MinPos, MaxPos)
	SetRoom("cont1_106", ROOM1, Room1Amount[0] + Floor(0.3 * Float(Room1Amount[1])), MinPos, MaxPos)
	SetRoom("cont1_096", ROOM1, Room1Amount[0] + Floor(0.4 * Float(Room1Amount[1])), MinPos, MaxPos)
	SetRoom("cont1_035", ROOM1, Room1Amount[0] + Floor(0.5 * Float(Room1Amount[1])), MinPos, MaxPos)
	SetRoom("cont1_895", ROOM1, Room1Amount[0] + Floor(0.7 * Float(Room1Amount[1])), MinPos, MaxPos)
	
	MinPos = Room2Amount[0]
	MaxPos = Room2Amount[0] + Room2Amount[1] - 1
	
	MapRoom(ROOM2, Room2Amount[0] + Floor(0.1 * Float(Room2Amount[1]))) = "room2_nuke"
	
	SetRoom("cont2_409", ROOM2, Room2Amount[0] + Floor(0.15 * Float(Room2Amount[1])), MinPos, MaxPos)
	SetRoom("room2_mt", ROOM2, Room2Amount[0] + Floor(0.25 * Float(Room2Amount[1])), MinPos, MaxPos)
	SetRoom("cont2_049", ROOM2, Room2Amount[0] + Floor(0.4 * Float(Room2Amount[1])), MinPos, MaxPos)
	SetRoom("cont2_008", ROOM2, Room2Amount[0] + Floor(0.5 * Float(Room2Amount[1])), MinPos, MaxPos)
	SetRoom("room2_shaft", ROOM2, Room2Amount[0] + Floor(0.6 * Float(Room2Amount[1])), MinPos, MaxPos)
	SetRoom("room2_test_hcz", ROOM2, Room2Amount[0] + Floor(0.7 * Float(Room2Amount[1])), MinPos, MaxPos)
	SetRoom("room2_servers_hcz", ROOM2, Room2Amount[0] + Floor(0.9 * Float(Room2Amount[1])), MinPos, MaxPos)
	
	MapRoom(ROOM2C, Room2CAmount[0] + Floor(0.5 * Float(Room2CAmount[1]))) = "room2c_maintenance"
	
	MapRoom(ROOM3, Room3Amount[0] + Floor(0.5 * Float(Room3Amount[1]))) = "cont3_513"
	MapRoom(ROOM3, Room3Amount[0] + Floor(0.8 * Float(Room3Amount[1]))) = "cont3_966"
	
	; ~ [ENTRANCE ZONE]
	
	MapRoom(ROOM1, Room1Amount[0] + Room1Amount[1] + Room1Amount[2] - 3) = "gate_b_entrance"
	MapRoom(ROOM1, Room1Amount[0] + Room1Amount[1] + Room1Amount[2] - 2) = "gate_a_entrance"
	MapRoom(ROOM1, Room1Amount[0] + Room1Amount[1] + Room1Amount[2] - 1) = "room1_o5"
	MapRoom(ROOM1, Room1Amount[0] + Room1Amount[1]) = "room1_lifts"
	
	MinPos = Room2Amount[0] + Room2Amount[1]
	MaxPos = Room2Amount[0] + Room2Amount[1] + Room2Amount[2] - 1
	
	MapRoom(ROOM2, MinPos + Floor(0.1 * Float(Room2Amount[2]))) = "room2_scientists"
	
	SetRoom("room2_cafeteria", ROOM2, MinPos + Floor(0.2 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2_6_ez", ROOM2, MinPos + Floor(0.25 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2_office_3", ROOM2, MinPos + Floor(0.3 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2_bio", ROOM2, MinPos + Floor(0.35 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2_servers_ez", ROOM2, MinPos + Floor(0.4 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2_office", ROOM2, MinPos + Floor(0.5 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2_office_2", ROOM2, MinPos + Floor(0.55 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("cont2_860_1", ROOM2, MinPos + Floor(0.6 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2_medibay", ROOM2, MinPos + Floor(0.7 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2_scientists_2", ROOM2, MinPos + Floor(0.8 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2_ic", ROOM2, MinPos + Floor(0.9 * Float(Room2Amount[2])), MinPos, MaxPos)
	
	MapRoom(ROOM2C, Room2CAmount[0] + Room2CAmount[1]) = "room2c_ec"
	MapRoom(ROOM2C, Room2CAmount[0] + Room2CAmount[1] + 1) = "room2c_gw_ez"
	
	MapRoom(ROOM3, Room3Amount[0] + Room3Amount[1] + Floor(0.3 * Float(Room3Amount[2]))) = "room3_2_ez"
	MapRoom(ROOM3, Room3Amount[0] + Room3Amount[1] + Floor(0.7 * Float(Room3Amount[2]))) = "room3_3_ez"
	MapRoom(ROOM3, Room3Amount[0] + Room3Amount[1] + Floor(0.5 * Float(Room3Amount[2]))) = "room3_office"
	
	; ~ [GENERATE OTHER ROOMS]
	
	Local CheckpointRoomID%, Checkpoint2RoomID%
	
	CheckpointRoomID = r_room2_checkpoint_lcz_hcz
	Checkpoint2RoomID = r_room2_checkpoint_hcz_ez
	
	Temp = 0
	For y = MapGridSize - 1 To 1 Step -1
		If y < (MapGridSize / 3) + 1
			Zone = EZ
		ElseIf y < MapGridSize * (2.0 / 3.0)
			Zone = HCZ
		Else
			Zone = LCZ
		EndIf
		For x = 1 To MapGridSize - 2
			If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_CheckpointTile
				If y > MapGridSize / 2
					If szl\CurrentZone = LCZ Lor szl\CurrentZone = HCZ Then r.Rooms = CreateRoom(szl\CurrentZone, ROOM2, x * RoomSpacing, 0.0, y * RoomSpacing, CheckpointRoomID)
				Else
					If szl\CurrentZone = HCZ Lor szl\CurrentZone = EZ Then r.Rooms = CreateRoom(szl\CurrentZone, ROOM2, x * RoomSpacing, 0.0, y * RoomSpacing, Checkpoint2RoomID)
				EndIf
				If Zone = szl\CurrentZone Then CalculateRoomExtents(r)
			ElseIf CurrMapGrid\Grid[x + (y * MapGridSize)] > MapGrid_NoTile
				Temp = Min(CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)], 1.0)
				Select(Temp)
					Case 1 ; ~ Generate ROOM1
						;[Block]
						If CurrMapGrid\RoomID[ROOM1] < MaxRooms And CurrMapGrid\RoomName[x + (y * MapGridSize)] = ""
							If MapRoom(ROOM1, CurrMapGrid\RoomID[ROOM1]) <> "" Then CurrMapGrid\RoomName[x + (y * MapGridSize)] = MapRoom(ROOM1, CurrMapGrid\RoomID[ROOM1])
						EndIf
						If Zone = szl\CurrentZone
							If CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)]
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 2
							ElseIf CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)]
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 3
							ElseIf CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)]
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 1
							Else
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 0
							EndIf
							RoomID = FindRoomID(CurrMapGrid\RoomName[x + (y * MapGridSize)])
							r.Rooms = CreateRoom(Zone, ROOM1, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID, CurrMapGrid\Angle[x + (y * MapGridSize)] * 90.0)
						EndIf
						CurrMapGrid\RoomID[ROOM1] = CurrMapGrid\RoomID[ROOM1] + 1
						;[End Block]
					Case 2 ; ~ Generate ROOM2
						;[Block]
						If CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] > MapGrid_NoTile
							If CurrMapGrid\RoomID[ROOM2] < MaxRooms And CurrMapGrid\RoomName[x + (y * MapGridSize)] = ""
								If MapRoom(ROOM2, CurrMapGrid\RoomID[ROOM2]) <> "" Then CurrMapGrid\RoomName[x + (y * MapGridSize)] = MapRoom(ROOM2, CurrMapGrid\RoomID[ROOM2])
							EndIf
							If Zone = szl\CurrentZone
								If Rand(2) = 1
									CurrMapGrid\Angle[x + (y * MapGridSize)] = 1
								Else
									CurrMapGrid\Angle[x + (y * MapGridSize)] = 3
								EndIf
								RoomID = FindRoomID(CurrMapGrid\RoomName[x + (y * MapGridSize)])
								r.Rooms = CreateRoom(Zone, ROOM2, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID, CurrMapGrid\Angle[x + (y * MapGridSize)] * 90.0)
							EndIf
							CurrMapGrid\RoomID[ROOM2] = CurrMapGrid\RoomID[ROOM2] + 1
						ElseIf CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile
							If CurrMapGrid\RoomID[ROOM2] < MaxRooms And CurrMapGrid\RoomName[x + (y * MapGridSize)] = ""
								If MapRoom(ROOM2, CurrMapGrid\RoomID[ROOM2]) <> "" Then CurrMapGrid\RoomName[x + (y * MapGridSize)] = MapRoom(ROOM2, CurrMapGrid\RoomID[ROOM2])
							EndIf
							If Zone = szl\CurrentZone
								If Rand(2) = 1
									CurrMapGrid\Angle[x + (y * MapGridSize)] = 2
								Else
									CurrMapGrid\Angle[x + (y * MapGridSize)] = 0
								EndIf
								RoomID = FindRoomID(CurrMapGrid\RoomName[x + (y * MapGridSize)])
								r.Rooms = CreateRoom(Zone, ROOM2, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID, CurrMapGrid\Angle[x + (y * MapGridSize)] * 90.0)
							EndIf
							CurrMapGrid\RoomID[ROOM2] = CurrMapGrid\RoomID[ROOM2] + 1
						Else
							If CurrMapGrid\RoomID[ROOM2C] < MaxRooms And CurrMapGrid\RoomName[x + (y * MapGridSize)] = ""
								If MapRoom(ROOM2C, CurrMapGrid\RoomID[ROOM2C]) <> "" Then CurrMapGrid\RoomName[x + (y * MapGridSize)] = MapRoom(ROOM2C, CurrMapGrid\RoomID[ROOM2C])
							EndIf
							If Zone = szl\CurrentZone
								If CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile
									CurrMapGrid\Angle[x + (y * MapGridSize)] = 2
								ElseIf CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile
									CurrMapGrid\Angle[x + (y * MapGridSize)] = 1
								ElseIf CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] > MapGrid_NoTile
									CurrMapGrid\Angle[x + (y * MapGridSize)] = 3
								Else
									CurrMapGrid\Angle[x + (y * MapGridSize)] = 0
								EndIf
								RoomID = FindRoomID(CurrMapGrid\RoomName[x + (y * MapGridSize)])
								r.Rooms = CreateRoom(Zone, ROOM2C, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID, CurrMapGrid\Angle[x + (y * MapGridSize)] * 90.0)
							EndIf
							CurrMapGrid\RoomID[ROOM2C] = CurrMapGrid\RoomID[ROOM2C] + 1
						EndIf
						;[End Block]
					Case 3 ; ~ Generate ROOM3
						;[Block]
						If CurrMapGrid\RoomID[ROOM3] < MaxRooms And CurrMapGrid\RoomName[x + (y * MapGridSize)] = ""
							If MapRoom(ROOM3, CurrMapGrid\RoomID[ROOM3]) <> "" Then CurrMapGrid\RoomName[x + (y * MapGridSize)] = MapRoom(ROOM3, CurrMapGrid\RoomID[ROOM3])
						EndIf
						If Zone = szl\CurrentZone Then
							If (Not CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)])
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 2
							ElseIf (Not CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)])
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 1
							ElseIf (Not CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)])
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 3
							Else
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 0
							EndIf
							RoomID = FindRoomID(CurrMapGrid\RoomName[x + (y * MapGridSize)])
							r.Rooms = CreateRoom(Zone, ROOM3, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID, CurrMapGrid\Angle[x + (y * MapGridSize)] * 90.0)
						EndIf
						CurrMapGrid\RoomID[ROOM3] = CurrMapGrid\RoomID[ROOM3] + 1
						;[End Block]
					Case 4 ; ~ Generate ROOM4
						;[Block]
						If CurrMapGrid\RoomID[ROOM4] < MaxRooms And CurrMapGrid\RoomName[x + (y * MapGridSize)] = ""
							If MapRoom(ROOM4, CurrMapGrid\RoomID[ROOM4]) <> "" Then CurrMapGrid\RoomName[x + (y * MapGridSize)] = MapRoom(ROOM4, CurrMapGrid\RoomID[ROOM4])
						EndIf
						If Zone = szl\CurrentZone
							CurrMapGrid\Angle[x + (y * MapGridSize)] = Rand(4)
							RoomID = FindRoomID(CurrMapGrid\RoomName[x + (y * MapGridSize)])
							r.Rooms = CreateRoom(Zone, ROOM4, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID, CurrMapGrid\Angle[x + (y * MapGridSize)] * 90.0)
						EndIf
						CurrMapGrid\RoomID[ROOM4] = CurrMapGrid\RoomID[ROOM4] + 1
						;[End Block]
				End Select
				If Zone = szl\CurrentZone Then CalculateRoomExtents(r)
			EndIf
		Next
	Next
;	Else ; ~ Spawn some rooms outside the generated map
;		If szl\CurrentZone = SURFACE
	r.Rooms = CreateRoom(0, ROOM1, (MapGridSize - 1) * RoomSpacing, 500.0, -(PowTwo(RoomSpacing)), r_gate_b)
	If Zone = szl\CurrentZone Then CalculateRoomExtents(r)
	CurrMapGrid\RoomID[ROOM1] = CurrMapGrid\RoomID[ROOM1] + 1
	
	r.Rooms = CreateRoom(0, ROOM1, (MapGridSize - 1) * RoomSpacing, 500.0, PowTwo(RoomSpacing), r_gate_a)
	If Zone = szl\CurrentZone Then CalculateRoomExtents(r)
	CurrMapGrid\RoomID[ROOM1] = CurrMapGrid\RoomID[ROOM1] + 1
;		ElseIf szl\CurrentZone <> SURFACE
	r.Rooms = CreateRoom(0, ROOM1, (MapGridSize - 1) * RoomSpacing, 0.0, (MapGridSize - 1) * RoomSpacing, r_dimension_106)
	If Zone = szl\CurrentZone Then CalculateRoomExtents(r)
	CurrMapGrid\RoomID[ROOM1] = CurrMapGrid\RoomID[ROOM1] + 1
	
	If opt\IntroEnabled
		r.Rooms = CreateRoom(0, ROOM1, RoomSpacing, 0.0, (MapGridSize - 1) * RoomSpacing, r_cont1_173_intro)
		If Zone = szl\CurrentZone Then CalculateRoomExtents(r)
		CurrMapGrid\RoomID[ROOM1] = CurrMapGrid\RoomID[ROOM1] + 1
	EndIf
;		EndIf
;	EndIf
	
	; ~ Prevent room overlaps
	For r.Rooms = Each Rooms
		PreventRoomOverlap(r)
	Next
	
	If opt\DebugMode
		ShowPointer()
		Repeat
			Cls()
			i = MapGridSize - 1
			For x = 0 To MapGridSize - 1
				For y = 0 To MapGridSize - 1
					If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_NoTile
						Zone = GetZone(y)
						Color((50 * Zone) + 50, (50 * Zone) + 50, (50 * Zone) + 50)
						Rect((i * 32) * MenuScale, (y * 32) * MenuScale, 30 * MenuScale, 30 * MenuScale)
					Else
						If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_CheckpointTile
							Color(0, 200, 0)
						ElseIf CurrMapGrid\Grid[x + (y * MapGridSize)] = 4
							Color(50, 50, 255)
						ElseIf CurrMapGrid\Grid[x + (y * MapGridSize)] = 3
							Color(50, 255, 255)
						ElseIf CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
							Color(255, 255, 50)
						Else
							Color(255, 255, 255)
						EndIf
						Rect((i * 32) * MenuScale, (y * 32) * MenuScale, 30 * MenuScale, 30 * MenuScale)
					EndIf
				Next
				i = i - 1
			Next
			
			i = MapGridSize - 1
			For x = 0 To MapGridSize - 1
				For y = 0 To MapGridSize - 1
					If MouseOn((i * 32) * MenuScale, (y * 32) * MenuScale, 32 * MenuScale, 32 * MenuScale)
						Color(255, 0, 0)
						TextEx(((i * 32) + 2) * MenuScale, ((y * 32) + 2) * MenuScale, CurrMapGrid\Grid[x + (y * MapGridSize)] + " " + CurrMapGrid\RoomName[x + (y * MapGridSize)])
					Else
						If CurrMapGrid\RoomName[x + (y * MapGridSize)] <> ""
							Color(0, 0, 0)
							TextEx(((i * 32) + 2) * MenuScale, ((y * 32) + 2) * MenuScale, CurrMapGrid\Grid[x + (y * MapGridSize)])
						EndIf
					EndIf
				Next
				i = i - 1
			Next
			Flip()
			RenderCursor()
		Until GetAnyKey();(GetKey() <> 0 Lor MouseHit(1))
	EndIf
	
	For y = 0 To MapGridSize
		For x = 0 To MapGridSize
			CurrMapGrid\Grid[x + (y * MapGridSize)] = Min(CurrMapGrid\Grid[x + (y * MapGridSize)], 1.0)
		Next
	Next
	
	; ~ Create the doors between rooms
	For y = MapGridSize To 0 Step -1
		If y < I_Zone\Transition[1] - 1
			Zone = EZ
		ElseIf y >= I_Zone\Transition[1] - 1 And y < I_Zone\Transition[0] - 1
			Zone = HCZ
		Else
			Zone = LCZ
		EndIf
		For x = MapGridSize To 0 Step -1
			If CurrMapGrid\Grid[x + (y * MapGridSize)] > MapGrid_NoTile
				For r.Rooms = Each Rooms
					r\Angle = WrapAngle(r\Angle)
					If Int(r\x / RoomSpacing) = x And Int(r\z / RoomSpacing) = y
						Select(r\RoomTemplate\Shape)
							Case ROOM1
								;[Block]
								ShouldSpawnDoor = (r\Angle = 90.0)
								;[End Block]
							Case ROOM2
								;[Block]
								ShouldSpawnDoor = (r\Angle = 90.0 Lor r\Angle = 270.0 )
								;[End Block]
							Case ROOM2C
								;[Block]
								ShouldSpawnDoor = (r\Angle = 0.0 Lor r\Angle = 90.0)
								;[End Block]
							Case ROOM3
								;[Block]
								ShouldSpawnDoor = (r\Angle = 0.0 Lor r\Angle = 180.0 Lor r\Angle = 90.0)
								;[End Block]
							Default
								;[Block]
								ShouldSpawnDoor = True
								;[End Block]
						End Select
						
						;[Block]
						If szl\CurrentZone = LCZ Then DoorID = LCZ_DOOR ElseIf szl\CurrentZone = HCZ Then DoorID = HCZ_DOOR ElseIf szl\CurrentZone = EZ Then DoorID = EZ_DOOR ElseIf szl\CurrentZone = RCZ Then DoorID = RCZ_DOOR ElseIf szl\CurrentZone = BCZ Then DoorID = ONE_SIDED_DOOR
						;[End Block]
						
						If ShouldSpawnDoor
							If x + 1 < MapGridSize + 1
								If CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] > MapGrid_NoTile
									d.Doors = CreateDoor(r, Float(x) * RoomSpacing + (RoomSpacing / 2.0), 0.0, Float(y) * RoomSpacing, 90.0, Max(Rand(-3, 1), 0.0), DoorID)
									r\AdjDoor[0] = d
								EndIf
							EndIf
						EndIf
						
						Select(r\RoomTemplate\Shape)
							Case ROOM1
								;[Block]
								ShouldSpawnDoor = (r\Angle = 180.0)
								;[End Block]
							Case ROOM2
								;[Block]
								ShouldSpawnDoor = (r\Angle = 0.0 Lor r\Angle = 180.0)
								;[End Block]
							Case ROOM2C
								;[Block]
								ShouldSpawnDoor = (r\Angle = 180.0 Lor r\Angle = 90.0)
								;[End Block]
							Case ROOM3
								;[Block]
								ShouldSpawnDoor = (r\Angle = 180.0 Lor r\Angle = 90.0 Lor r\Angle = 270.0)
								;[End Block]
							Default
								;[Block]
								ShouldSpawnDoor = True
								;[End Block]
						End Select
						If ShouldSpawnDoor
							If y + 1 < MapGridSize + 1
								If CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile
									d.Doors = CreateDoor(r, Float(x) * RoomSpacing, 0.0, Float(y) * RoomSpacing + (RoomSpacing / 2.0), 0.0, Max(Rand(-3, 1), 0.0), DoorID)
									r\AdjDoor[3] = d
								EndIf
							EndIf
						EndIf
						Exit
					EndIf
				Next
			EndIf
		Next
	Next
	
	For r.Rooms = Each Rooms
		r\Angle = WrapAngle(r\Angle)
		;SetupTriggerBoxes(r)
		For i = 0 To MaxRoomAdjacents - 1
			r\Adjacent[i] = Null
		Next
		For r2.Rooms = Each Rooms
			If r <> r2
				If r2\z = r\z
					If r2\x = r\x + 8.0
						r\Adjacent[0] = r2
						If r\AdjDoor[0] = Null Then r\AdjDoor[0] = r2\AdjDoor[2]
					ElseIf r2\x = r\x - 8.0
						r\Adjacent[2] = r2
						If r\AdjDoor[2] = Null Then r\AdjDoor[2] = r2\AdjDoor[0]
					EndIf
				ElseIf r2\x = r\x
					If r2\z = r\z - 8.0
						r\Adjacent[1] = r2
						If r\AdjDoor[1] = Null Then r\AdjDoor[1] = r2\AdjDoor[3]
					ElseIf r2\z = r\z + 8.0
						r\Adjacent[3] = r2
						If r\AdjDoor[3] = Null Then r\AdjDoor[3] = r2\AdjDoor[1]
					EndIf
				EndIf
			EndIf
			If r\Adjacent[0] <> Null And r\Adjacent[1] <> Null And r\Adjacent[2] <> Null And r\Adjacent[3] <> Null Then Exit
		Next
	Next
End Function

Function LoadTerrain%(HeightMap%, yScale# = 0.7, Tex1%, Tex2%, Mask%)
	; ~ Load the HeightMap
	If HeightMap = 0 Then RuntimeError(Format(GetLocalString("runerr", "heightmap"), HeightMap))
	; ~ Load texture and lightmaps
	If Tex1 = 0 Then RuntimeError(Format(GetLocalString("runerr", "tex_1"), Tex1))
	If Tex2 = 0 Then RuntimeError(Format(GetLocalString("runerr", "tex_2"), Tex2))
	If Mask = 0 Then RuntimeError(Format(GetLocalString("runerr", "mask"), Mask))
	
	; ~ Store HeightMap dimensions
	Local HeightMapWidth% = ImageWidth(HeightMap) - 1
	Local HeightMapHeight% = ImageHeight(HeightMap) - 1
	Local PosX%, PosY%, VertexIndex%
	
	; ~ Scale the textures to the right size
	ScaleTexture(Tex1, HeightMapWidth / 4.0, HeightMapHeight / 4.0)
	ScaleTexture(Tex2, HeightMapWidth / 4.0, HeightMapHeight / 4.0)
	ScaleTexture(Mask, HeightMapWidth, HeightMapHeight)
	
	; ~ Start building the terrain
	Local Mesh% = CreateMesh()
	Local Surf% = CreateSurface(Mesh)
	
	; ~ Create some verts for the terrain
	For PosY = 0 To HeightMapHeight
		For PosX = 0 To HeightMapWidth
			AddVertex(Surf, PosX, 0.0, PosY, 1.0 / PosX, 1.0 / PosY)
		Next
	Next
	RenderWorld()
	
	Local HeightMapWidth2% = HeightMapWidth + 1
	
	; ~ Connect the verts with faces
	For PosY = 0 To HeightMapHeight - 1
		For PosX = 0 To HeightMapWidth - 1
			Local Shift% = PosX + (HeightMapWidth2 * PosY)
			
			AddTriangle(Surf, Shift, Shift + HeightMapWidth2, Shift + 1)
			AddTriangle(Surf, Shift + 1, Shift + HeightMapWidth2, Shift + HeightMapWidth2 + 1)
		Next
	Next
	
	; ~ Position the terrain to center 0.0, 0.0, 0.0
	Local Mesh2% = CopyMesh(Mesh, Mesh)
	Local Surf2% = GetSurface(Mesh2, 1)
	
	PositionMesh(Mesh, (-HeightMapWidth) / 2.0, 0.0, (-HeightMapHeight) / 2.0)
	PositionMesh(Mesh2, (-HeightMapWidth) / 2.0, 0.01, (-HeightMapHeight) / 2.0)
	
	Local HeightMapBuffer% = ImageBuffer(HeightMap)
	Local MaskBuffer% = TextureBuffer(Mask)
	Local MaskWidth% = TextureWidth(Mask)
	Local MaskHeight% = TextureHeight(Mask)
	
	; ~ Alter vertice height to match the heightmap red channel
	LockBuffer(HeightMapBuffer)
	LockBuffer(MaskBuffer)
	
	For PosX = 0 To HeightMapWidth
		For PosY = 0 To HeightMapHeight
			; ~ Using vertex alpha and two meshes instead of FE_ALPHAWHATEVER
			; ~ It doesn't look perfect but it does the job
			; ~ You might get better results by downscaling the mask to the same size as the heightmap
			Local MaskX# = Min(PosX * Float(MaskWidth) / Float(HeightMapWidth2), MaskWidth - 1)
			Local MaskY# = MaskHeight - Min(PosY * Float(MaskHeight) / Float(HeightMapHeight + 1), MaskHeight - 1)
			Local RGB%, RED%
			
			RGB = ReadPixelFast(Min(PosX, HeightMapWidth - 1.0), HeightMapHeight - Min(PosY, HeightMapHeight - 1.0), HeightMapBuffer)
			RED = (RGB And $FF0000) Shr 16 ; ~ Separate out the red
			
			Local Alpha# = (((ReadPixelFast(Max(MaskX -5.0, 5.0), Max(MaskY - 5.0, 5.0), MaskBuffer) And $FF000000) Shr 24) / $FF)
			
			Alpha = Alpha + (((ReadPixelFast(Min(MaskX + 5.0, MaskWidth - 5.0), Min(MaskY + 5.0, MaskHeight - 5), MaskBuffer) And $FF000000) Shr 24) / $FF)
			Alpha = Alpha + (((ReadPixelFast(Max(MaskX - 5.0, 5.0), Min(MaskY + 5.0, MaskHeight - 5.0), MaskBuffer) And $FF000000) Shr 24) / $FF)
			Alpha = Alpha + (((ReadPixelFast(Min(MaskX + 5.0, MaskWidth - 5.0), Max(MaskY - 5.0, 5.0), MaskBuffer) And $FF000000) Shr 24) / $FF)
			Alpha = Alpha * 0.25
			Alpha = Sqr(Alpha)
			
			VertexIndex = PosX + (HeightMapWidth2 * PosY)
			VertexCoords(Surf, VertexIndex , VertexX(Surf, VertexIndex), RED * yScale, VertexZ(Surf, VertexIndex))
			VertexCoords(Surf2, VertexIndex , VertexX(Surf2, VertexIndex), RED * yScale, VertexZ(Surf2, VertexIndex))
			VertexColor(Surf2, VertexIndex, 255.0, 255.0, 255.0, Alpha)
			; ~ Set the terrain texture coordinates
			VertexTexCoords(Surf, VertexIndex, PosX, -PosY)
			VertexTexCoords(Surf2, VertexIndex, PosX, -PosY) 
		Next
	Next
	UnlockBuffer(MaskBuffer)
	UnlockBuffer(HeightMapBuffer)
	
	UpdateNormals(Mesh)
	UpdateNormals(Mesh2)
	
	EntityTexture(Mesh, Tex1, 0, 0)
	EntityTexture(Mesh2, Tex2, 0, 0)
	
	EntityFX(Mesh, 1)
	EntityFX(Mesh2, 1 + 2 + 32)
	
	Return(Mesh)
End Function

RenderLoading(60, GetLocalString("loading", "core.sky"))

Include "Source Code\Sky_Core.bb"
;~IDEal Editor Parameters:
;~C#Blitz3D TSS