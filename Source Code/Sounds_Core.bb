Function PlaySound2%(SoundHandle%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0, IsVoice% = False)
	Range = Max(Range, 1.0)
	
	Local SoundCHN% = 0
	
	If Volume > 0.0
		Local Dist# = EntityDistance(Cam, Entity) / Range
		
		If (1.0 - Dist > 0.0) And (1.0 - Dist < 1.0)
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			SoundCHN = PlaySound_Strict(SoundHandle, IsVoice)
			
			ChannelVolume(SoundCHN, Volume * (1.0 - Dist) * ((opt\VoiceVolume * IsVoice) + (opt\SFXVolume * (Not (IsVoice)))) * opt\MasterVolume)
			ChannelPan(SoundCHN, PanValue)
		EndIf
	Else
		ChannelVolume(SoundCHN, 0.0)
	EndIf
	Return(SoundCHN)
End Function

Function LoopSound2%(SoundHandle%, SoundCHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0, IsVoice% = False)
	Range = Max(Range, 1.0)
	
	If Volume > 0.0
		Local Dist# = EntityDistance(Cam, Entity) / Range
		Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
		
		If (Not ChannelPlaying(SoundCHN)) Then SoundCHN = PlaySound_Strict(SoundHandle, IsVoice)
		
		ChannelVolume(SoundCHN, Volume * (1.0 - Dist) * ((opt\VoiceVolume * IsVoice) + (opt\SFXVolume * (Not (IsVoice)))) * opt\MasterVolume)
		ChannelPan(SoundCHN, PanValue)
	Else
		ChannelVolume(SoundCHN, 0.0)
	EndIf
	Return(SoundCHN)
End Function

Function UpdateSoundOrigin%(SoundCHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0, IsVoice% = False, SFXVolume% = True)
	If ChannelPlaying(SoundCHN)
		Range = Max(Range, 1.0)
		
		If Volume > 0.0
			Local Dist# = EntityDistance(Cam, Entity) / Range
			
			If (1.0 - Dist > 0.0) And (1.0 - Dist < 1.0)
				Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
				
				ChannelVolume(SoundCHN, Volume * (1.0 - Dist) * ((Not SFXVolume) + (SFXVolume * ((opt\VoiceVolume * IsVoice) + (opt\SFXVolume * (Not (IsVoice)))) * opt\MasterVolume)))
				ChannelPan(SoundCHN, PanValue)
			Else
				ChannelVolume(SoundCHN, 0.0)
			EndIf
		Else
			ChannelVolume(SoundCHN, 0.0)
		EndIf
	EndIf
End Function

Function PlayMTFSound%(SoundHandle%, n.NPCs)
	If n <> Null Then n\SoundCHN = PlaySound2(SoundHandle, Camera, n\Collider, 8.0, 1.0, True)
	If IsUsingRadio
		If SelectedItem\State2 = 3.0 And SelectedItem\State > 0.0
			If SoundHandle <> NPCSound[SOUND_NPC_MTF_BEEP] Lor (Not ChannelPlaying(RadioCHN[3]))
				StopChannel(RadioCHN[3]) : RadioCHN[3] = 0
				RadioCHN[3] = PlaySound_Strict(SoundHandle, True)
			EndIf
		EndIf
	EndIf
End Function

Function LoadEventSound%(e.Events, File$, Number% = 0)
	If Number = 0
		If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
		e\Sound = LoadSound_Strict(File)
		Return(e\Sound)
	ElseIf Number = 1
		If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
		e\Sound2 = LoadSound_Strict(File)
		Return(e\Sound2)
	EndIf
End Function

Function LoadNPCSound%(n.NPCs, File$, Number% = 0)
	If Number = 0
		If n\Sound <> 0 Then FreeSound_Strict(n\Sound) : n\Sound = 0
		n\Sound = LoadSound_Strict(File)
		Return(n\Sound)
	ElseIf Number = 1
		If n\Sound2 <> 0 Then FreeSound_Strict(n\Sound2) : n\Sound2 = 0
		n\Sound2 = LoadSound_Strict(File)
		Return(n\Sound2)
	EndIf
End Function

Function LoadTempSound%(File$)
	Local TempSound%
	
	If TempSounds[TempSoundIndex] <> 0 Then FreeSound_Strict(TempSounds[TempSoundIndex]) : TempSounds[TempSoundIndex] = 0
	TempSound = LoadSound_Strict(File)
	TempSounds[TempSoundIndex] = TempSound
	TempSoundIndex = ((TempSoundIndex + 1) Mod 10)
	Return(TempSound)
End Function

Function UpdateMusic%()
	If NowPlaying <> ShouldPlay ; ~ Playing the wrong clip, fade out
		opt\CurrMusicVolume = Max(opt\CurrMusicVolume - (fps\Factor[0] / 250.0), 0.0)
		If opt\CurrMusicVolume = 0.0
			If NowPlaying < MUS_NULL Then StopStream_Strict(MusicCHN) : MusicCHN = 0
			NowPlaying = ShouldPlay
			CurrMusic = False
		EndIf
	Else ; ~ Playing the right clip
		opt\CurrMusicVolume = opt\CurrMusicVolume + (opt\MusicVolume - opt\CurrMusicVolume) * (0.1 * fps\Factor[0])
	EndIf
	
	If NowPlaying < MUS_NULL
		If (Not CurrMusic)
			MusicCHN = StreamSound_Strict("SFX\Music\" + Music[NowPlaying] + ".ogg", 0.0, Mode)
			CurrMusic = True
		EndIf
		SetStreamVolume_Strict(MusicCHN, opt\CurrMusicVolume * opt\MasterVolume)
	EndIf
End Function

Function PauseSounds%()
	Local e.Events, n.NPCs, d.Doors, sc.SecurityCams, em.Emitters, r.Rooms, se.SoundEmitters, elev.Elevator
	Local i%
	
	For e.Events = Each Events
		If e\SoundCHN_IsStream
			If e\SoundCHN <> 0 Then SetStreamPaused_Strict(e\SoundCHN, True)
		Else
			PauseChannel(e\SoundCHN)
		EndIf
		If e\SoundCHN2_IsStream
			If e\SoundCHN2 <> 0 Then SetStreamPaused_Strict(e\SoundCHN2, True)
		Else
			PauseChannel(e\SoundCHN2)
		EndIf
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN_IsStream
			If n\SoundCHN <> 0 Then SetStreamPaused_Strict(n\SoundCHN, True)
		Else
			PauseChannel(n\SoundCHN)
		EndIf
		If n\SoundCHN2_IsStream
			If n\SoundCHN2 <> 0 Then SetStreamPaused_Strict(n\SoundCHN2, True)
		Else
			PauseChannel(n\SoundCHN2)
		EndIf
	Next
	
	For d.Doors = Each Doors
		PauseChannel(d\SoundCHN)
		PauseChannel(d\SoundCHN2)
		PauseChannel(d\ButtonCHN)
	Next
	
	For sc.SecurityCams = Each SecurityCams
		PauseChannel(sc\SoundCHN)
	Next
	
	For em.Emitters = Each Emitters
		PauseChannel(em\SoundCHN)
	Next
	
	For r.Rooms = Each Rooms
		PauseChannel(r\SoundCHN)
	Next
	
	For se.SoundEmitters = Each SoundEmitters
		PauseChannel(se\SoundCHN)
	Next
	
	PauseChannel(AmbientSFXCHN)
	PauseChannel(BreathCHN)
	PauseChannel(BreathGasRelaxedCHN)
	PauseChannel(VomitCHN)
	PauseChannel(CoughCHN)
	PauseChannel(SCRAMBLECHN)
	PauseChannel(wep_I\SoundCHN)
	
	For elev.Elevator = Each Elevator
		PauseChannel(elev\soundchn)
	Next
	
	For i = 0 To 1
		PauseChannel(LowBatteryCHN[i])
	Next
	
	For i = 0 To 6
		PauseChannel(RadioCHN[i])
	Next
	
	If IntercomStreamCHN <> 0 Then SetStreamPaused_Strict(IntercomStreamCHN, True)
End Function

Function ResumeSounds%()
	Local e.Events, n.NPCs, d.Doors, sc.SecurityCams, em.Emitters, r.Rooms, se.SoundEmitters, elev.Elevator
	Local i%
	
	For e.Events = Each Events
		If e\SoundCHN_IsStream
			If e\SoundCHN <> 0 Then SetStreamPaused_Strict(e\SoundCHN, False)
		Else
			ResumeChannel(e\SoundCHN)
		EndIf
		If e\SoundCHN2_IsStream
			If e\SoundCHN2 <> 0 Then SetStreamPaused_Strict(e\SoundCHN2, False)
		Else
			ResumeChannel(e\SoundCHN2)
		EndIf
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN_IsStream
			If n\SoundCHN <> 0 Then SetStreamPaused_Strict(n\SoundCHN, False)
		Else
			ResumeChannel(n\SoundCHN)
		EndIf
		If n\SoundCHN2_IsStream
			If n\SoundCHN2 <> 0 Then SetStreamPaused_Strict(n\SoundCHN2, False)
		Else
			ResumeChannel(n\SoundCHN2)
		EndIf
	Next
	
	For d.Doors = Each Doors
		ResumeChannel(d\SoundCHN)
		ResumeChannel(d\SoundCHN2)
		ResumeChannel(d\ButtonCHN)
	Next
	
	For sc.SecurityCams = Each SecurityCams
		ResumeChannel(sc\SoundCHN)
	Next
	
	For em.Emitters = Each Emitters
		ResumeChannel(em\SoundCHN)
	Next
	
	For r.Rooms = Each Rooms
		ResumeChannel(r\SoundCHN)
	Next
	
	For se.SoundEmitters = Each SoundEmitters
		ResumeChannel(se\SoundCHN)
	Next
	
	ResumeChannel(AmbientSFXCHN)
	ResumeChannel(BreathCHN)
	ResumeChannel(BreathGasRelaxedCHN)
	ResumeChannel(VomitCHN)
	ResumeChannel(CoughCHN)
	ResumeChannel(SCRAMBLECHN)
	ResumeChannel(wep_I\SoundCHN)
	
	For elev.Elevator = Each Elevator
		ResumeChannel(elev\soundchn)
	Next
	
	For i = 0 To 1
		ResumeChannel(LowBatteryCHN[i])
	Next
	
	If IsUsingRadio
		For i = 0 To 5
			If SelectedItem\State2 = i Then ResumeChannel(RadioCHN[i])
		Next
		StopChannel(RadioCHN[6]) : RadioCHN[6] = 0
	EndIf
	
	If IntercomStreamCHN <> 0 Then SetStreamPaused_Strict(IntercomStreamCHN, False)
End Function

Function KillSounds%(EraseSounds% = True)
	Local e.Events, n.NPCs, d.Doors, snd.Sound, sc.SecurityCams, em.Emitters, r.Rooms, se.SoundEmitters, elev.Elevator
	Local i%
	
	For i = 0 To 8 Step 2
		If TempSounds[i] <> 0 Then FreeSound_Strict(TempSounds[i]) : TempSounds[i] = 0
		If TempSounds[i + 1] <> 0 Then FreeSound_Strict(TempSounds[i + 1]) : TempSounds[i + 1] = 0
	Next
	
	For e.Events = Each Events
		If e\SoundCHN_IsStream
			If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN_IsStream = False
		Else
			StopChannel(e\SoundCHN)
		EndIf
		e\SoundCHN = 0
		If e\SoundCHN2_IsStream
			If e\SoundCHN2 <> 0 Then StopStream_Strict(e\SoundCHN2) : e\SoundCHN2_IsStream = False
		Else
			StopChannel(e\SoundCHN2)
		EndIf
		e\SoundCHN2 = 0
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN_IsStream
			If n\SoundCHN <> 0 Then StopStream_Strict(n\SoundCHN) : n\SoundCHN_IsStream = False
		Else
			StopChannel(n\SoundCHN)
		EndIf
		n\SoundCHN = 0
		If n\SoundCHN2_IsStream
			If n\SoundCHN2 <> 0 Then StopStream_Strict(n\SoundCHN2) : n\SoundCHN2_IsStream = False
		Else
			StopChannel(n\SoundCHN2)
		EndIf
		n\SoundCHN2 = 0
	Next
	
	For d.Doors = Each Doors
		StopChannel(d\SoundCHN) : d\SoundCHN = 0
		StopChannel(d\SoundCHN2) : d\SoundCHN2 = 0
		StopChannel(d\ButtonCHN) : d\ButtonCHN = 0
	Next
	
	For sc.SecurityCams = Each SecurityCams
		StopChannel(sc\SoundCHN) : sc\SoundCHN = 0
	Next
	
	For em.Emitters = Each Emitters
		StopChannel(em\SoundCHN) : em\SoundCHN = 0
	Next
	
	For r.Rooms = Each Rooms
		StopChannel(r\SoundCHN) : r\SoundCHN = 0
	Next
	
	For se.SoundEmitters = Each SoundEmitters
		StopChannel(se\SoundCHN) : se\SoundCHN = 0
	Next
	
	StopChannel(AmbientSFXCHN) : AmbientSFXCHN = 0
	StopChannel(BreathCHN) : BreathCHN = 0
	StopChannel(BreathGasRelaxedCHN) : BreathGasRelaxedCHN = 0
	StopChannel(VomitCHN) : VomitCHN = 0
	StopChannel(CoughCHN) : CoughCHN = 0
	StopChannel(SCRAMBLECHN) : SCRAMBLECHN = 0
	StopChannel(wep_I\SoundCHN) : wep_I\SoundCHN = 0
	
	For elev.Elevator = Each Elevator
		StopChannel(elev\soundchn) : elev\soundchn = 0
	Next
	
	For i = 0 To 1
		StopChannel(LowBatteryCHN[i]) : LowBatteryCHN[i] = 0
	Next
		
	For i = 0 To 6
		StopChannel(RadioCHN[i]) : RadioCHN[i] = 0
	Next
	
	If IntercomStreamCHN <> 0 Then StopStream_Strict(IntercomStreamCHN) : IntercomStreamCHN = 0
	
	If EraseSounds
		If opt\EnableSFXRelease
			For snd.Sound = Each Sound
				If snd\InternalHandle <> 0
					FreeSound(snd\InternalHandle) : snd\InternalHandle = 0
					RemoveSubtitlesToken(snd)
				EndIf
				snd\ReleaseTime = 0
			Next
		EndIf
	EndIf
	
	For snd.Sound = Each Sound
		For i = 0 To MaxChannelsAmount - 1
			StopChannel(snd\Channels[i]) : snd\Channels[i] = 0
		Next
	Next
	
	ClearSubtitles()
End Function

Function StopBreathSound%()
	If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN) : BreathCHN = 0
	If ChannelPlaying(BreathGasRelaxedCHN) Then StopChannel(BreathGasRelaxedCHN) : BreathGasRelaxedCHN = 0
End Function

Function GetStepSound%(Entity%)
	Local mat.Materials
	Local Picker%, Brush%, Texture%, Name$
	Local i%
	
	Picker = LinePick(EntityX(Entity), EntityY(Entity), EntityZ(Entity), 0.0, -1.0, 0.0)
	If Picker <> 0
		If GetEntityType(Picker) <> HIT_MAP Then Return(0)
		Brush = GetSurfaceBrush(GetSurface(Picker, CountSurfaces(Picker)))
		If Brush <> 0
			For i = 3 To 1 Step -1
				Texture = GetBrushTexture(Brush, i)
				If Texture <> 0
					Name = StripPath(TextureName(Texture))
					
					If Name <> "" 
					    FreeTexture(Texture) : Texture = 0
					    
						For mat.Materials = Each Materials
							If mat\Name = Name
								FreeBrush(Brush) : Brush = 0
								If mat\StepSound > 0 Then Return(mat\StepSound - 1)
								Exit
							EndIf
						Next
					EndIf
				EndIf
			Next
			; ~ If we reach this point then no step sound found, free the brush
			FreeBrush(Brush) : Brush = 0
		EndIf
	EndIf
	Return(0)
End Function

Function PlayAnnouncement%(File$) ; ~ This function streams the announcement currently playing
	If (Not PlayerInReachableRoom(True, True)) Then Return
	
	If IntercomStreamCHN <> 0 Then StopStream_Strict(IntercomStreamCHN) : IntercomStreamCHN = 0
	IntercomStreamCHN = StreamSound_Strict(File, opt\VoiceVolume * opt\MasterVolume, 0)
End Function

Function UpdateStreamSounds%()
	Local e.Events
	
	If fps\Factor[0] > 0.0
		If IntercomStreamCHN <> 0 Then SetStreamVolume_Strict(IntercomStreamCHN, opt\VoiceVolume * opt\MasterVolume)
		For e.Events = Each Events
			If e\SoundCHN_IsStream
				If e\SoundCHN <> 0 Then SetStreamVolume_Strict(e\SoundCHN, opt\SFXVolume * opt\MasterVolume)
			EndIf
			If e\SoundCHN2_IsStream
				If e\SoundCHN2 <> 0 Then SetStreamVolume_Strict(e\SoundCHN2, opt\SFXVolume * opt\MasterVolume)
			EndIf
		Next
	EndIf
	
	If (Not PlayerInReachableRoom(True))
		If (Not IsPlayerOutsideFacility())
			If IntercomStreamCHN <> 0 Then StopStream_Strict(IntercomStreamCHN) : IntercomStreamCHN = 0
			If PlayerRoom\RoomTemplate\RoomID <> r_dimension_1499
				For e.Events = Each Events
					If e\SoundCHN_IsStream
						If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
					EndIf
					If e\SoundCHN2_IsStream
						If e\SoundCHN2 <> 0 Then StopStream_Strict(e\SoundCHN2) : e\SoundCHN2 = 0 : e\SoundCHN2_IsStream = False
					EndIf
				Next
			EndIf
		EndIf
	EndIf
End Function

Function ControlSoundVolume%()
	Local snd.Sound
	Local i%
	
	For snd.Sound = Each Sound
		For i = 0 To MaxChannelsAmount - 1
			ChannelVolume(snd\Channels[i], opt\SFXVolume * opt\MasterVolume)
		Next
	Next
End Function

Function UpdateDeaf%()
	If me\DeafTimer > 0.0
		me\DeafTimer = Max(me\DeafTimer - fps\Factor[0], 0.0)
		opt\MasterVolume = 0.0
		If opt\MasterVolume > 0.0 Then ControlSoundVolume()
	Else
		opt\MasterVolume = opt\PrevMasterVolume
		If me\Deaf Then ControlSoundVolume()
		me\Deaf = False
	EndIf
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS