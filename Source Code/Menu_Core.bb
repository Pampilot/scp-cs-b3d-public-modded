
; ~ Story Mode Constants
;[Block]
Const MaxStoryModes% = 3
Const Menu_Ryan% = 0, Menu_NTF% = 1, Menu_Class_D% = 2
;[End Block]

; ~ Menu Secrets Constants
;[Block]
Const MenuSecret_Wolfnaya% = 1
;[End Block]

;[Block]
Type MainMenu
	Field MainMenuBlinkTimer#[2]
	Field MainMenuBlinkDuration#[2]
	Field MainMenuStr$, MainMenuStrX%, MainMenuStrY%
	Field MainMenuTab%, PrevMainMenuTab%
	Field CurrMenuPage%
	Field PressedSecret% = 0
End Type
;[End Block]

Global mm.MainMenu

;[Block]
Type MainMenuAssets
	Field BackGround%
	Field LogoImg%
	Field StoryIMG%[MaxStoryModes]
	Field ChapterIMG%[MaxStoryModes]
End Type
;[End Block]

;[Block]
Type CreditsAssets
	Field EndingScreen%, EndingTimer#
	Field CreditsScreen%, CreditsTimer#, CreditsTextTimer#
End Type
;[End Block]

Global cra.CreditsAssets = New CreditsAssets

Global mma.MainMenuAssets

Global MainMenuOpen%

Function InitMainMenuAssets%()
	mm.MainMenu = New MainMenu
	mma.MainMenuAssets = New MainMenuAssets
	
	mma\BackGround = LoadImage_Strict("GFX\Menu\Background.png")
	mma\BackGround = ScaleImage2(mma\BackGround, MenuScale, MenuScale)
	
	mma\LogoImg = LoadImage_Strict("GFX\Menu\Logos\SCP-CS.png")
	mma\LogoImg = ScaleImage2(mma\LogoImg, MenuScale, MenuScale)
	
	mma\StoryIMG[Menu_Ryan] = LoadImage_Strict("GFX\Menu\Stories\Story_Ryan.png") : mma\StoryIMG[Menu_Ryan] = ScaleImage2(mma\StoryIMG[Menu_Ryan], MenuScale, MenuScale) : ResizeImage(mma\StoryIMG[Menu_Ryan], (157 * MenuScale), (157 * MenuScale))
	mma\StoryIMG[Menu_NTF] = LoadImage_Strict("GFX\Menu\Stories\Story_NTF.png") : mma\StoryIMG[Menu_NTF] = ScaleImage2(mma\StoryIMG[Menu_NTF], MenuScale, MenuScale) : ResizeImage(mma\StoryIMG[Menu_NTF], (157 * MenuScale), (157 * MenuScale))
	mma\StoryIMG[Menu_Class_D] = LoadImage_Strict("GFX\Menu\Stories\Story_Class_D.png") : mma\StoryIMG[Menu_Class_D] = ScaleImage2(mma\StoryIMG[Menu_Class_D], MenuScale, MenuScale) : ResizeImage(mma\StoryIMG[Menu_Class_D], (157 * MenuScale), (157 * MenuScale))
	
	mm\MainMenuBlinkTimer[0] = 1.0
	mm\MainMenuBlinkTimer[1] = 1.0
	
	ButtonSFX[0] = LoadSound_Strict("SFX\Interact\Button.ogg")
	ButtonSFX[1] = LoadSound_Strict("SFX\Interact\Button2.ogg")
End Function

Function DeInitMainMenuAssets%()
	Local i%
	
	FreeImage(mma\BackGround) : mma\BackGround = 0
	FreeImage(mma\LogoImg) : mma\LogoImg = 0
	For i = 0 To MaxStoryModes - 1
		FreeImage(mma\StoryIMG[i]) : mma\StoryIMG[i] = 0
	Next
	Delete Each MainMenuAssets
	Delete Each MainMenu
End Function

Global RandomSeed$

Global SelectedInputBox%, CursorPos% = -1
Global ShouldDeleteGadgets%

; ~ Main Menu Tab Constants
;[Block]
Const MainMenuTab_Default% = 0
Const MainMenuTab_Start_Game% = 1
Const MainMenuTab_Story_Mode% = 2
Const MainMenuTab_Select_Game% = 3
Const MainMenuTab_Mission_Mode% = 4
Const MainMenuTab_Sandbox_Mode% = 5
Const MainMenuTab_New_Game% = 6
Const MainMenuTab_Load_Game% = 7
Const MainMenuTab_Load_Map% = 8
Const MainMenuTab_Extras% = 9
Const MainMenuTab_Extras_Achievements% = 10
Const MainMenuTab_Options% = 11
Const MainMenuTab_Options_Graphics% = 12
Const MainMenuTab_Options_Audio% = 13
Const MainMenuTab_Options_Controls% = 14
Const MainMenuTab_Options_Controls_Tab% = 15
Const MainMenuTab_Options_Advanced% = 16
;[End Block]

Function ChangeOptionTab%(Page%, MainMenu% = True)
	If MainMenu
		mm\MainMenuTab = Page
	Else
		igm\OptionsMenu = Page
		ShouldDeleteGadgets = True
	EndIf
	ResetInput()
End Function

Function ChangePage%(Page%)
	mm\CurrMenuPage = Page
	ShouldDeleteGadgets = True
End Function

If opt\DisplayMode <> 0
	opt\AntiAliasing = False
	IniWriteString(OptionFile, "Graphics", "Anti-Aliasing", opt\AntiAliasing)
EndIf

Function UpdateMainMenu%()
	CatchErrors("UpdateMainMenu()")
	
	Local sv.Save, cm.CustomMaps, snd.Sound
	Local x%, y%, Width%, Height%, Temp%, i%, n%, j%
	Local File$, Test%
	
	If MainMenuOpen
		UpdateConsole(1)
		
		If KeyHit(key\CONSOLE)
			If opt\CanOpenConsole
				If ConsoleOpen
					UsedConsole = True
					StopMouseMovement()
					ShouldDeleteGadgets = True
				EndIf
				ConsoleOpen = (Not ConsoleOpen)
				FlushKeys()
			EndIf
		EndIf
	EndIf
	
	While fps\Accumulator > 0.0
		fps\Accumulator = fps\Accumulator - TICK_DURATION
		
		UpdateMouseInput()
		
		If ShouldDeleteGadgets Then DeleteMenuGadgets()
		ShouldDeleteGadgets = False
		
		UpdateMusic()
		If opt\EnableSFXRelease Then AutoReleaseSounds()
		
		Select(mm\PressedSecret)
			Case 0
				;[Block]
				If ShouldPlay = MUS_MENU_BREATH
					EndBreathSFX = LoadSound_Strict("SFX\Ending\MenuBreath.ogg")
					EndBreathCHN = PlaySound_Strict(EndBreathSFX, True)
					ShouldPlay = MUS_NULL
				ElseIf ShouldPlay = MUS_NULL
					If (Not ChannelPlaying(EndBreathCHN))
						FreeSound_Strict(EndBreathSFX)
						ShouldPlay = MUS_MAIN_MENU
					EndIf
				Else
					ShouldPlay = MUS_MAIN_MENU
				EndIf
				;[End Block]
			Case MenuSecret_Wolfnaya
				;[Block]
				ShouldPlay = MUS_SAVE_ME_FROM
				;[End Block]
		End Select
		
		If Rand(300) = 1
			mm\MainMenuBlinkTimer[0] = Rnd(4000.0, 8000.0)
			mm\MainMenuBlinkDuration[0] = Rnd(200.0, 500.0)
		EndIf
		
		mm\MainMenuBlinkTimer[1] = mm\MainMenuBlinkTimer[1] - fps\Factor[0]
		
		If (Not mo\MouseDown1) Then OnSliderID = 0
		
		If mm\PrevMainMenuTab <> mm\MainMenuTab Then DeleteMenuGadgets() : mm\CurrMenuPage = 0
		mm\PrevMainMenuTab = mm\MainMenuTab
		
		If mm\MainMenuTab = MainMenuTab_Default
			y = 286 * MenuScale
			Width = 400 * MenuScale
			Height = 70 * MenuScale
			x = mo\Viewport_Center_X - (Width / 2)
			
			If UpdateMenuButton(x, y, Width, Height, GetLocalString("menu", "stgame"), Font_Default_Big) Then mm\MainMenuTab = MainMenuTab_Start_Game
			
			y = y + (100 * MenuScale)
			
			If UpdateMenuButton(x, y, Width, Height, GetLocalString("menu", "options"), Font_Default_Big) Then mm\MainMenuTab = MainMenuTab_Options
			
			y = y + (100 * MenuScale)
			
			If UpdateMenuButton(x, y, Width, Height, GetLocalString("menu", "extras"), Font_Default_Big) Then mm\MainMenuTab = MainMenuTab_Extras
			
			y = y + (100 * MenuScale)
			
			If UpdateMenuButton(x, y, Width, Height, GetLocalString("menu", "quit"), Font_Default_Big)
				StopStream_Strict(MusicCHN) : MusicCHN = 0
				End()
			EndIf
		Else
			y = 376 * MenuScale
			Width = 580 * MenuScale
			x = mo\Viewport_Center_X - (Width / 2)
			If mm\MainMenuTab < MainMenuTab_Options_Graphics
				Select(mm\MainMenuTab)
					Case MainMenuTab_Start_Game
						;[Block]
						Height = 60 * MenuScale
						
						If UpdateMenuButton(x + (20 * MenuScale), y + (15 * MenuScale), (Width / 5) + (420 * MenuScale), Height, GetLocalString("menu", "strmd"), Font_Default_Big) Then mm\MainMenuTab = MainMenuTab_Select_Game
						If UpdateMenuButton(x + (20 * MenuScale), y + (85  * MenuScale), (Width / 5) + (420 * MenuScale), Height, GetLocalString("menu", "msnmd"), Font_Default_Big) Then mm\MainMenuTab = MainMenuTab_Mission_Mode
						If UpdateMenuButton(x + (20 * MenuScale), y + (155 * MenuScale), (Width / 5) + (420 * MenuScale), Height, GetLocalString("menu", "sndbmd"), Font_Default_Big) Then mm\MainMenuTab = MainMenuTab_Sandbox_Mode
						;[End Block]
					Case MainMenuTab_Select_Game
						;[Block]
						Height = 60 * MenuScale
						
						RandomSeed = ""
						If UpdateMenuButton(x + (20 * MenuScale), y + (15 * MenuScale), (Width / 5) + (420 * MenuScale), Height, GetLocalString("menu", "new"), Font_Default_Big)
							If opt\DebugMode
								RandomSeed = "666"
							Else
								If Rand(15) = 1
									Select(Rand(13))
										Case 1
											;[Block]
											RandomSeed = "NIL"
											;[End Block]
										Case 2
											;[Block]
											RandomSeed = "NO"
											;[End Block]
										Case 3
											;[Block]
											RandomSeed = "d9341"
											;[End Block]
										Case 4
											;[Block]
											RandomSeed = "5CP_I73"
											;[End Block]
										Case 5
											;[Block]
											RandomSeed = "DONTBLINK"
											;[End Block]
										Case 6
											;[Block]
											RandomSeed = "CRUNCH"
											;[End Block]
										Case 7
											;[Block]
											RandomSeed = "die"
											;[End Block]
										Case 8
											;[Block]
											RandomSeed = "HTAED"
											;[End Block]
										Case 9
											;[Block]
											RandomSeed = "rustledjim"
											;[End Block]
										Case 10
											;[Block]
											RandomSeed = "larry"
											;[End Block]
										Case 11
											;[Block]
											RandomSeed = "JORGE"
											;[End Block]
										Case 12
											;[Block]
											RandomSeed = "dirtymetal"
											;[End Block]
										Case 13
											;[Block]
											RandomSeed = "whatpumpkin"
											;[End Block]
									End Select
								Else
									i = Rand(4, 8)
									For j = 1 To i
										If Rand(3) = 1
											RandomSeed = RandomSeed + Rand(0, 9)
										Else
											RandomSeed = RandomSeed + Chr(Rand(97, 122))
										EndIf
									Next
								EndIf
							EndIf
							LoadSavedGames()
							CurrSave = New Save
							LoadCustomMaps()
							CurrCustomMap = New CustomMaps
							mm\MainMenuTab = MainMenuTab_Story_Mode
						EndIf
						
						If UpdateMenuButton(x + (20 * MenuScale), y + (85  * MenuScale), (Width / 5) + (420 * MenuScale), Height, GetLocalString("menu", "load"), Font_Default_Big)
							LoadSavedGames()
							mm\MainMenuTab = MainMenuTab_Load_Game
						EndIf
						;[End Block]
					Case MainMenuTab_Story_Mode
						;[Block]
						Height = 165 * MenuScale
						
						If UpdateMenuButton(x + (20 * MenuScale), y + (15 * MenuScale), Height, Height, "", Font_Default_Big)
							gm\ID = GM_RYAN
							mm\MainMenuTab = MainMenuTab_New_Game
						EndIf
						If UpdateMenuButton(x + (40 * MenuScale) + Height, y + (15 * MenuScale), Height, Height, "", Font_Default_Big)
							gm\ID = GM_NTF
							mm\MainMenuTab = MainMenuTab_New_Game
						EndIf
						If UpdateMenuButton(x + (60 * MenuScale) + (Height * 2), y + (15 * MenuScale), Height, Height, "", Font_Default_Big)
							gm\ID = GM_CLASS_D
							mm\MainMenuTab = MainMenuTab_New_Game
						EndIf
						;[End Block]
					Case MainMenuTab_New_Game
						;[Block]
						Height = 345 * MenuScale
						
						CurrSave\Name = UpdateMenuInputBox(x + (150 * MenuScale), y + (15 * MenuScale), 200 * MenuScale, 30 * MenuScale, CurrSave\Name, Font_Default, 1, 15)
						If SelectedInputBox = 1
							CurrSave\Name = Replace(CurrSave\Name, ":", "")
							CurrSave\Name = Replace(CurrSave\Name, ".", "")
							CurrSave\Name = Replace(CurrSave\Name, "/", "")
							CurrSave\Name = Replace(CurrSave\Name, "\", "")
							CurrSave\Name = Replace(CurrSave\Name, "<", "")
							CurrSave\Name = Replace(CurrSave\Name, ">", "")
							CurrSave\Name = Replace(CurrSave\Name, "|", "")
							CurrSave\Name = Replace(CurrSave\Name, "?", "")
							CurrSave\Name = Replace(CurrSave\Name, Chr(34), "")
							CurrSave\Name = Replace(CurrSave\Name, "*", "")
							CursorPos = Min(CursorPos, Len(CurrSave\Name))
						EndIf
						
						If SelectedCustomMap = Null
							RandomSeed = UpdateMenuInputBox(x + (150 * MenuScale), y + (55 * MenuScale), 200 * MenuScale, 30 * MenuScale, RandomSeed, Font_Default, 2, 15)
						Else
							If UpdateMenuButton(x + (370 * MenuScale), y + (55 * MenuScale), 120 * MenuScale, 30 * MenuScale, GetLocalString("menu", "deselect"))
								ShouldDeleteGadgets = True
								SelectedCustomMap = Null
							EndIf
						EndIf
						
						opt\IntroEnabled = UpdateMenuTick(x + (280 * MenuScale), y + (110 * MenuScale), opt\IntroEnabled)
						
						For i = SAFE To ESOTERIC
							Local PrevSelectedDifficulty.Difficulty = SelectedDifficulty
							
							If UpdateMenuTick(x + (20 * MenuScale), y + ((180 + 30 * i) * MenuScale), (SelectedDifficulty = difficulties[i])) Then SelectedDifficulty = difficulties[i]
							
							If PrevSelectedDifficulty <> SelectedDifficulty Then ShouldDeleteGadgets = (PrevSelectedDifficulty = difficulties[ESOTERIC])
						Next
						
						If SelectedDifficulty\Customizable
							; ~ Save type
							If UpdateMenuButton(x + (160 * MenuScale), y + (180 * MenuScale), 20 * MenuScale, 20 * MenuScale, ">")
								If SelectedDifficulty\SaveType < NO_SAVES
									SelectedDifficulty\SaveType = SelectedDifficulty\SaveType + 1
								Else
									SelectedDifficulty\SaveType = SAVE_ANYWHERE
								EndIf
							EndIf
							
							; ~ Agressive NPCs
							SelectedDifficulty\AggressiveNPCs = UpdateMenuTick(x + (160 * MenuScale), y + (210 * MenuScale), SelectedDifficulty\AggressiveNPCs)
							
							; ~ Realism
							SelectedDifficulty\Realism = UpdateMenuTick(x + (160 * MenuScale), y + (240 * MenuScale), SelectedDifficulty\Realism)
							
							; ~ Other factor's difficulty
							If UpdateMenuButton(x + (160 * MenuScale), y + (270 * MenuScale), 20 * MenuScale, 20 * MenuScale, ">")
								If SelectedDifficulty\OtherFactors < EXTREME
									SelectedDifficulty\OtherFactors = SelectedDifficulty\OtherFactors + 1
								Else
									SelectedDifficulty\OtherFactors = EASY
								EndIf
							EndIf
						EndIf
						
						If UpdateMenuButton(x, y + Height + (20 * MenuScale), 160 * MenuScale, 75 * MenuScale, GetLocalString("menu", "loadmap"))
							LoadCustomMaps()
							mm\MainMenuTab = MainMenuTab_Load_Map
						EndIf
						
						If UpdateMenuButton(x + (420 * MenuScale), y + Height + (20 * MenuScale), 160 * MenuScale, 75 * MenuScale, GetLocalString("menu", "start"))
							If CurrSave\Name = "" Then CurrSave\Name = ConvertToANSI(GetLocalString("save", "untitled"))
							
							If RandomSeed = "" Then RandomSeed = MilliSec
							
							SeedRnd(GenerateSeedNumber(RandomSeed))
							
							Local SameFound% = 0
							Local LowestPossible% = 2
							
							For sv.Save = Each Save
								If (CurrSave <> sv And CurrSave\Name = sv\Name)
									SameFound = 1
									Exit
								EndIf
							Next
							
							While SameFound = 1
								SameFound = 2
								For sv.Save = Each Save
									If (sv\Name = (CurrSave\Name + " (" + LowestPossible + ")"))
										LowestPossible = LowestPossible + 1
										SameFound = 1
										Exit
									EndIf
								Next
							Wend
							
							If SameFound = 2 Then CurrSave\Name = CurrSave\Name + " (" + LowestPossible + ")"
							
							;[Block]
							szl\CurrentZone = LCZ
							;[End Block]
							
							InitNewGame()
							
							IniWriteString(OptionFile, "Global", "Enable Intro", opt\IntroEnabled)
							
							MainMenuOpen = False
							Return
						EndIf
						;[End Block]
					Case MainMenuTab_Load_Game
						;[Block]
						Height = 296 * MenuScale
						
						If mm\CurrMenuPage < Ceil(Float(SavedGamesAmount) / 5.0) - 1 And DelSave = Null
							If UpdateMenuButton(x + Width - (50 * MenuScale), y + (440 * MenuScale), 50 * MenuScale, 50 * MenuScale, ">", Font_Default_Big) Then ChangePage(mm\CurrMenuPage + 1)
						Else
							UpdateMenuButton(x + Width - (50 * MenuScale), y + (440 * MenuScale), 50 * MenuScale, 50 * MenuScale, ">", Font_Default_Big, False, True)
						EndIf
						If mm\CurrMenuPage > 0 And DelSave = Null
							If UpdateMenuButton(x, y + (440 * MenuScale), 50 * MenuScale, 50 * MenuScale, "<", Font_Default_Big) Then ChangePage(mm\CurrMenuPage - 1)
						Else
							UpdateMenuButton(x, y + (440 * MenuScale), 50 * MenuScale, 50 * MenuScale, "<", Font_Default_Big, False, True)
						EndIf
						If mm\CurrMenuPage > Ceil(Float(SavedGamesAmount) / 5.0) - 1 Then ChangePage(mm\CurrMenuPage - 1)
						
						If SavedGamesAmount > 0
							x = x + (20 * MenuScale)
							y = y + (20 * MenuScale)
							
							CurrSave = First Save
							
							For i = 0 To 4 + (5 * mm\CurrMenuPage)
								If i > 0 Then CurrSave = After CurrSave
								If CurrSave = Null Then Exit
								If i >= (5 * mm\CurrMenuPage)
									If DelSave = Null
										If CurrSave\Version <> VersionNumber
											UpdateMenuButton(x + (280 * MenuScale), y + (20 * MenuScale), 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "btnload"), Font_Default, False, True, 255, 0, 0)
										Else
											If UpdateMenuButton(x + (280 * MenuScale), y + (20 * MenuScale), 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "btnload"))
												LoadEntities()
												LoadSounds()
												LoadGame(CurrSave\Name)
												InitLoadGame()
												ShouldDeleteGadgets = True
												MainMenuOpen = False
												Return
											EndIf
										EndIf
										
										If UpdateMenuButton(x + (400 * MenuScale), y + (20 * MenuScale), 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "delete"))
											DelSave = CurrSave
											Exit
										EndIf
									Else
										If CurrSave\Version <> VersionNumber
											UpdateMenuButton(x + (280 * MenuScale), y + (20 * MenuScale), 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "btnload"), Font_Default, False, True, 255, 0, 0)
										Else
											UpdateMenuButton(x + (280 * MenuScale), y + (20 * MenuScale), 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "btnload"), Font_Default, False, True)
										EndIf
										UpdateMenuButton(x + (400 * MenuScale), y + (20 * MenuScale), 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "delete"), Font_Default, False, True)
									EndIf
									If CurrSave = Last Save Then Exit
									y = y + (80 * MenuScale)
								EndIf
							Next
							
							If DelSave <> Null
								y = 376 * MenuScale
								Width = 100 * MenuScale
								x = mo\Viewport_Center_X - (Width / 2)
								x = x + 340 * MenuScale
								
								If UpdateMenuButton(x + (74 * MenuScale), y + (150 * MenuScale), 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "yes"))
									DeleteGame(DelSave)
									ShouldDeleteGadgets = True
								EndIf
								If UpdateMenuButton(x + (246 * MenuScale), y + (150 * MenuScale), 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "no"))
									DelSave = Null
									ShouldDeleteGadgets = True
								EndIf
							EndIf
						EndIf
						;[End Block]
					Case MainMenuTab_Sandbox_Mode
						;[Block]
						Height = 345 * MenuScale
						
						LoadSavedGames()
						CurrSave = New Save
						
						RandomSeed = UpdateMenuInputBox(x + (150 * MenuScale), y + (35 * MenuScale), 200 * MenuScale, 30 * MenuScale, RandomSeed, Font_Default, 2, 15)
						
						If UpdateMenuButton(x + (420 * MenuScale), y + Height + (20 * MenuScale), 160 * MenuScale, 75 * MenuScale, GetLocalString("menu", "play"))
							If CurrSave\Name = "" Then CurrSave\Name = ConvertToANSI(GetLocalString("save", "untitled"))
							
							If RandomSeed = "" Then RandomSeed = MilliSec
							
							SeedRnd(GenerateSeedNumber(RandomSeed))
							
							SameFound% = 0
							LowestPossible% = 2
							
							For sv.Save = Each Save
								If (CurrSave <> sv And CurrSave\Name = sv\Name)
									SameFound = 1
									Exit
								EndIf
							Next
							
							While SameFound = 1
								SameFound = 2
								For sv.Save = Each Save
									If (sv\Name = (CurrSave\Name + " (" + LowestPossible + ")"))
										LowestPossible = LowestPossible + 1
										SameFound = 1
										Exit
									EndIf
								Next
							Wend
							
							If SameFound = 2 Then CurrSave\Name = CurrSave\Name + " (" + LowestPossible + ")"
							
							SelectedDifficulty\SaveType = NO_SAVES
							
							gm\ID = GM_SANDBOX
							
							InitNewGame()
							
							MainMenuOpen = False
							Return
						EndIf
						
						If UpdateMenuButton(x + (160 * MenuScale), y + (180 * MenuScale), 350 * MenuScale, 20 * MenuScale, GetLocalString("menu", "selzone") + " LCZ") Then szl\CurrentZone = LCZ
						If UpdateMenuButton(x + (160 * MenuScale), y + (210 * MenuScale), 350 * MenuScale, 20 * MenuScale, GetLocalString("menu", "selzone") + " HCZ") Then szl\CurrentZone = HCZ
						If UpdateMenuButton(x + (160 * MenuScale), y + (240 * MenuScale), 350 * MenuScale, 20 * MenuScale, GetLocalString("menu", "selzone") + " EZ") Then szl\CurrentZone = EZ
					Case MainMenuTab_Options
						;[Block]
						Height = 60 * MenuScale
						
						If UpdateMenuButton(x + (20 * MenuScale), y + (15 * MenuScale), (Width / 5) + (420 * MenuScale), Height, GetLocalString("options", "grap"), Font_Default_Big) Then ChangeOptionTab(MainMenuTab_Options_Graphics)
						If UpdateMenuButton(x + (20 * MenuScale), y + (85  * MenuScale), (Width / 5) + (420 * MenuScale), Height, GetLocalString("options", "audio"), Font_Default_Big) Then ChangeOptionTab(MainMenuTab_Options_Audio)
						If UpdateMenuButton(x + (20 * MenuScale), y + (155 * MenuScale), (Width / 5) + (420 * MenuScale), Height, GetLocalString("options", "ctrl"), Font_Default_Big) Then ChangeOptionTab(MainMenuTab_Options_Controls)
						If UpdateMenuButton(x + (20 * MenuScale), y + (225 * MenuScale), (Width / 5) + (420 * MenuScale), Height, GetLocalString("options", "avc"), Font_Default_Big) Then ChangeOptionTab(MainMenuTab_Options_Advanced)
						;[End Block]
					Case MainMenuTab_Extras
						;[Block]
						Height = 60 * MenuScale
						
						If UpdateMenuButton(x + (20 * MenuScale), y + (15 * MenuScale), (Width / 5) + (420 * MenuScale), Height, GetLocalString("menu", "achv"), Font_Default_Big) Then mm\MainMenuTab = MainMenuTab_Extras_Achievements
						If UpdateMenuButton(x + (20 * MenuScale), y + (85  * MenuScale), (Width / 5) + (420 * MenuScale), Height, GetLocalString("menu", "credits"), Font_Default_Big) Then LoadCredits()
;						If UpdateMenuButton(x + (20 * MenuScale), y + (155 * MenuScale), (Width / 5) + (420 * MenuScale), Height, GetLocalString("options", "..."), Font_Default_Big) Then mm\MainMenuTab = MainMenuTab_Sandbox_Mode
						;[End Block]
				End Select
			Else
				Height = 60 * MenuScale
				Width = 580 * MenuScale
				x = mo\Viewport_Center_X - (Width / 2)
				x = x + 310 * MenuScale
				
				Select(mm\MainMenuTab)
					Case MainMenuTab_Options_Graphics
						;[Block]
						y = y + (20 * MenuScale)
						
						opt\BumpEnabled = UpdateMenuTick(x, y, opt\BumpEnabled)
						
						y = y + (30 * MenuScale)
						
						opt\VSync = UpdateMenuTick(x, y, opt\VSync)
						
						y = y + (30 * MenuScale)
						
						opt\AntiAliasing = UpdateMenuTick(x, y, opt\AntiAliasing, opt\DisplayMode <> 0)
						
						y = y + (30 * MenuScale)
						
						opt\AdvancedRoomLights = UpdateMenuTick(x, y, opt\AdvancedRoomLights)
						
						y = y + (40 * MenuScale)
						
						opt\ScreenGamma = UpdateMenuSlideBar(x, y, 150 * MenuScale, opt\ScreenGamma * 50.0, 1) / 50.0
						
						y = y + (45 * MenuScale)
						
						opt\ParticleAmount = UpdateMenuSlider3(x, y, 150 * MenuScale, opt\ParticleAmount, 2, GetLocalString("options", "min"), GetLocalString("options", "red"), GetLocalString("options", "full"))
						
						y = y + (45 * MenuScale)
						
						opt\TextureDetails = UpdateMenuSlider5(x, y, 150 * MenuScale, opt\TextureDetails, 3, "0.8", "0.4", "0.0", "-0.4", "-0.8")
						Select(opt\TextureDetails)
							Case 0
								;[Block]
								opt\TextureDetailsLevel = 0.8
								;[End Block]
							Case 1
								;[Block]
								opt\TextureDetailsLevel = 0.4
								;[End Block]
							Case 2
								;[Block]
								opt\TextureDetailsLevel = 0.0
								;[End Block]
							Case 3
								;[Block]
								opt\TextureDetailsLevel = -0.4
								;[End Block]
							Case 4
								;[Block]
								opt\TextureDetailsLevel = -0.8
								;[End Block]
						End Select
						TextureLodBias(opt\TextureDetailsLevel)
						
						y = y + (35 * MenuScale)
						
						opt\SaveTexturesInVRAM = UpdateMenuTick(x, y, opt\SaveTexturesInVRAM)
						
						y = y + (40 * MenuScale)
						
						opt\CurrFOV = (UpdateMenuSlideBar(x, y, 150 * MenuScale, opt\CurrFOV * 2.0, 4) / 2.0)
						opt\FOV = opt\CurrFOV + 40
						
						y = y + (45 * MenuScale)
						
						opt\Anisotropic = UpdateMenuSlider5(x, y, 150 * MenuScale, opt\Anisotropic, 5, GetLocalString("options", "tri"), "2x", "4x", "8x", "16x")
						Select(opt\Anisotropic)
							Case 0
								;[Block]
								opt\AnisotropicLevel = 0
								;[End Block]
							Case 1
								;[Block]
								opt\AnisotropicLevel = 2
								;[End Block]
							Case 2
								;[Block]
								opt\AnisotropicLevel = 4
								;[End Block]
							Case 3
								;[Block]
								opt\AnisotropicLevel = 8
								;[End Block]
							Case 4
								;[Block]
								opt\AnisotropicLevel = 16
								;[End Block]
						End Select
						TextureAnisotropic(opt\AnisotropicLevel)
						
						y = y + (35 * MenuScale)
						
						opt\Atmosphere = UpdateMenuTick(x, y, opt\Atmosphere)
						
						y = y + (45 * MenuScale)
						
						opt\SecurityCamRenderInterval = UpdateMenuSlider5(x, y, 150 * MenuScale, opt\SecurityCamRenderInterval, 17, "24.0", "18.0", "12.0", "6.0", "0.0")
						Select(opt\SecurityCamRenderInterval)
							Case 0
								;[Block]
								opt\SecurityCamRenderIntervalLevel = 24.0
								;[End Block]
							Case 1
								;[Block]
								opt\SecurityCamRenderIntervalLevel = 18.0
								;[End Block]
							Case 2
								;[Block]
								opt\SecurityCamRenderIntervalLevel = 12.0
								;[End Block]
							Case 3
								;[Block]
								opt\SecurityCamRenderIntervalLevel = 6.0
								;[End Block]
							Case 4
								;[Block]
								opt\SecurityCamRenderIntervalLevel = 0.0
								;[End Block]
						End Select
						;[End Block]
					Case MainMenuTab_Options_Audio
						;[Block]
						y = 376 * MenuScale
						Width = 580 * MenuScale
						Height = 60 * MenuScale
						x = mo\Viewport_Center_X - (Width / 2)
						x = x + 310 * MenuScale
						
						y = y + (20 * MenuScale)
						
						opt\PrevMasterVolume = UpdateMenuSlideBar(x, y, 150 * MenuScale, opt\MasterVolume * 100.0, 1) / 100.0
						opt\MasterVolume = opt\PrevMasterVolume
						
						y = y + (40 * MenuScale)
						
						opt\MusicVolume = UpdateMenuSlideBar(x, y, 150 * MenuScale, opt\MusicVolume * 100.0, 2) / 100.0
						
						y = y + (40 * MenuScale)
						
						opt\SFXVolume = UpdateMenuSlideBar(x, y, 150 * MenuScale, opt\SFXVolume * 100.0, 3) / 100.0
						
						y = y + (40 * MenuScale)
						
						opt\VoiceVolume = UpdateMenuSlideBar(x, y, 150 * MenuScale, opt\VoiceVolume * 100.0, 18) / 100.0
						
						y = y + (40 * MenuScale)
						
						opt\EnableSFXRelease = UpdateMenuTick(x, y, opt\EnableSFXRelease)
						If opt\PrevEnableSFXRelease <> opt\EnableSFXRelease
							If opt\EnableSFXRelease
								For snd.Sound = Each Sound
									For i = 0 To MaxChannelsAmount - 1
										StopChannel(snd\Channels[i]) : snd\Channels[i] = 0
									Next
									If snd\InternalHandle <> 0 Then FreeSound(snd\InternalHandle) : snd\InternalHandle = 0
									snd\ReleaseTime = 0
								Next
							Else
								For snd.Sound = Each Sound
									If snd\InternalHandle = 0 Then snd\InternalHandle = LoadSound(snd\Name)
								Next
							EndIf
							opt\PrevEnableSFXRelease = opt\EnableSFXRelease
						EndIf
						
						y = y + (30 * MenuScale)
						
						Local PrevEnableUserTracks% = opt\EnableUserTracks
						
						opt\EnableUserTracks = UpdateMenuTick(x, y, opt\EnableUserTracks)
						
						If PrevEnableUserTracks Then ShouldDeleteGadgets = (PrevEnableUserTracks <> opt\EnableUserTracks)
						
						If opt\EnableUserTracks
							y = y + (30 * MenuScale)
							
							opt\UserTrackMode = UpdateMenuTick(x, y, opt\UserTrackMode)
							
							If UpdateMenuButton(x - (290 * MenuScale), y + (30 * MenuScale), 220 * MenuScale, 30 * MenuScale, GetLocalString("options", "scantracks"))
								UserTrackCheck = 0
								UserTrackCheck2 = 0
								
								Local DirPath$ = "SFX\Radio\UserTracks\"
								
								If FileType(DirPath) <> 2 Then CreateDir(DirPath)
								
								Local Dir% = ReadDir(DirPath)
								
								Repeat
									File = NextFile(Dir)
									If File = "" Then Exit
									If FileType(DirPath + File) = 1
										UserTrackCheck = UserTrackCheck + 1
										Test = LoadSound(DirPath + File)
										If Test <> 0 Then UserTrackCheck2 = UserTrackCheck2 + 1
										FreeSound(Test) : Test = 0
									EndIf
								Forever
								CloseDir(Dir)
							EndIf
							
							y = y + (40 * MenuScale)
						Else
							UserTrackCheck = 0
						EndIf
						
						y = y + (30 * MenuScale)
						
						Local PrevEnableSubtitles% = opt\EnableSubtitles
						Local PrevOverrideSubColor% = opt\OverrideSubColor
						
						opt\EnableSubtitles = UpdateMenuTick(x, y, opt\EnableSubtitles)
						
						If opt\EnableSubtitles
							y = y + (30 * MenuScale)
							
							opt\OverrideSubColor = UpdateMenuTick(x, y, opt\OverrideSubColor)
						EndIf
						
						If PrevEnableSubtitles Lor PrevOverrideSubColor Then ShouldDeleteGadgets = (PrevEnableSubtitles <> opt\EnableSubtitles) Lor (PrevOverrideSubColor <> opt\OverrideSubColor)
						
						If opt\EnableSubtitles And opt\OverrideSubColor
							y = y + (35 * MenuScale)
							
							UpdateMenuPalette(x - (63 * MenuScale), y)
							
							y = y + (30 * MenuScale)
							
							opt\SubColorR = Min(UpdateMenuInputBox(x - (135 * MenuScale), y, 40 * MenuScale, 20 * MenuScale, Str(Int(opt\SubColorR)), Font_Default, 14, 3), 255.0)
							
							y = y + (30 * MenuScale)
							
							opt\SubColorG = Min(UpdateMenuInputBox(x - (135 * MenuScale), y, 40 * MenuScale, 20 * MenuScale, Str(Int(opt\SubColorG)), Font_Default, 15, 3), 255.0)
							
							y = y + (30 * MenuScale)
							
							opt\SubColorB = Min(UpdateMenuInputBox(x - (135 * MenuScale), y, 40 * MenuScale, 20 * MenuScale, Str(Int(opt\SubColorB)), Font_Default, 16, 3), 255.0)
							
							y = y + (40 * MenuScale)
						EndIf
						;[End Block]
					Case MainMenuTab_Options_Controls
						;[Block]
						y = y + (20 * MenuScale)
						
						opt\MouseSensitivity = (UpdateMenuSlideBar(x, y, 150 * MenuScale, (opt\MouseSensitivity + 0.5) * 100.0, 1) / 100.0) - 0.5
						
						y = y + (40 * MenuScale)
						
						opt\InvertMouseX = UpdateMenuTick(x, y, opt\InvertMouseX)
						
						y = y + (40 * MenuScale)
						
						opt\InvertMouseY = UpdateMenuTick(x, y, opt\InvertMouseY)
						
						y = y + (40 * MenuScale)
						
						opt\MouseSmoothing = UpdateMenuSlideBar(x, y, 150 * MenuScale, (opt\MouseSmoothing) * 50.0, 2) / 50.0
						
						y = y + (40 * MenuScale)
						
						If UpdateMenuButton(x, y, 200 * MenuScale, 20 * MenuScale, GetLocalString("options", "ctrl.tab")) Then mm\MainMenuTab = MainMenuTab_Options_Controls_Tab
						;[End Block]
					Case MainMenuTab_Options_Controls_Tab
						;[Block]
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x - (150 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_UP, 210.0)], Font_Default, 3)
						UpdateMenuInputBox(x + (140 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\RELOAD, 210.0)], Font_Default, 12)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x - (150 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_LEFT, 210.0)], Font_Default, 4)
						UpdateMenuInputBox(x + (140 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\CHECKAMMO, 210.0)], Font_Default, 13)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x - (150 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_DOWN, 210.0)], Font_Default, 5)
						UpdateMenuInputBox(x + (140 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\INSPECT, 210.0)], Font_Default, 14)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x - (150 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_RIGHT, 210.0)], Font_Default, 6)
						UpdateMenuInputBox(x + (140 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\FIREMODE, 210.0)], Font_Default, 15)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x - (150 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\INTERACT, 210.0)], Font_Default, 7)
						UpdateMenuInputBox(x + (140 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\LOW, 210.0)], Font_Default, 16)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x - (150 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SPRINT, 210.0)], Font_Default, 8)
						UpdateMenuInputBox(x + (140 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\HOLSTER, 210.0)], Font_Default, 17)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x - (150 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\CROUCH, 210.0)], Font_Default, 9)
						UpdateMenuInputBox(x + (140 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SAVE, 210.0)], Font_Default, 18)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x - (150 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\BLINK, 210.0)], Font_Default, 10)
						UpdateMenuInputBox(x + (140 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\LOAD, 210.0)], Font_Default, 19)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x - (150 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\INVENTORY, 210.0)], Font_Default, 11)
						UpdateMenuInputBox(x + (140 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SCREENSHOT, 210.0)], Font_Default, 20)
						
						y = y + (20 * MenuScale)
						
						If opt\CanOpenConsole Then UpdateMenuInputBox(x + (140 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\CONSOLE, 210.0)], Font_Default, 21)
						
						Local TempKey%
						
						For i = 0 To 227
							If KeyHit(i)
								TempKey = i
								Exit
							EndIf
						Next
						If TempKey <> 0
							Select(SelectedInputBox)
								Case 3
									;[Block]
									key\MOVEMENT_UP = TempKey
									;[End Block]
								Case 4
									;[Block]
									key\MOVEMENT_LEFT = TempKey
									;[End Block]
								Case 5
									;[Block]
									key\MOVEMENT_DOWN = TempKey
									;[End Block]
								Case 6
									;[Block]
									key\MOVEMENT_RIGHT = TempKey
									;[End Block]
								Case 7
									;[Block]
									key\INTERACT = TempKey
									;[End Block]
								Case 8
									;[Block]
									key\SPRINT = TempKey
									;[End Block]
								Case 9
									;[Block]
									key\CROUCH = TempKey
									;[End Block]
								Case 10
									;[Block]
									key\BLINK = TempKey
									;[End Block]
								Case 11
									;[Block]
									key\INVENTORY = TempKey
									;[End Block]
								Case 12
									;[Block]
									key\RELOAD = TempKey
									;[End Block]
								Case 13
									;[Block]
									key\CHECKAMMO = TempKey
									;[End Block]
								Case 14
									;[Block]
									key\INSPECT = TempKey
									;[End Block]
								Case 15
									;[Block]
									key\FIREMODE = TempKey
									;[End Block]
								Case 16
									;[Block]
									key\LOW = TempKey
									;[End Block]
								Case 17
									;[Block]
									key\HOLSTER = TempKey
									;[End Block]
								Case 18
									;[Block]
									key\SAVE = TempKey
									;[End Block]
								Case 19
									;[Block]
									key\LOAD = TempKey
									;[End Block]
								Case 20
									;[Block]
									key\SCREENSHOT = TempKey
									;[End Block]
								Case 21
									;[Block]
									key\CONSOLE = TempKey
									;[End Block]
							End Select
							SelectedInputBox = 0
						EndIf
						;[End Block]
					Case MainMenuTab_Options_Advanced
						;[Block]
						y = y + (20 * MenuScale)
						
						opt\HUDEnabled = UpdateMenuTick(x, y, opt\HUDEnabled)
						
						y = y + (30 * MenuScale)
						
						Local PrevCanOpenConsole% = opt\CanOpenConsole
						
						opt\CanOpenConsole = UpdateMenuTick(x, y, opt\CanOpenConsole)
						
						If PrevCanOpenConsole Then ShouldDeleteGadgets = (PrevCanOpenConsole <> opt\CanOpenConsole)
						
						y = y + (30 * MenuScale)
						
						If opt\CanOpenConsole Then opt\ConsoleOpening = UpdateMenuTick(x, y, opt\ConsoleOpening)
						
						y = y + (30 * MenuScale)
						
						opt\AchvMsgEnabled = UpdateMenuTick(x, y, opt\AchvMsgEnabled)
						
						y = y + (30 * MenuScale)
						
						opt\AutoSaveEnabled = UpdateMenuTick(x, y, opt\AutoSaveEnabled, SelectedDifficulty\SaveType <> SAVE_ANYWHERE)
						
						y = y + (30 * MenuScale)
						
						opt\TextShadow = UpdateMenuTick(x, y, opt\TextShadow)
						
						y = y + (30 * MenuScale)
						
						opt\ShowFPS = UpdateMenuTick(x, y, opt\ShowFPS)
						
						y = y + (30 * MenuScale)
						
						Local PrevCurrFrameLimit% = opt\CurrFrameLimit > 0.0
						
						If UpdateMenuTick(x, y, opt\CurrFrameLimit > 0.0)
							opt\CurrFrameLimit = UpdateMenuSlideBar(x - (160 * MenuScale), y + (40 * MenuScale), 150 * MenuScale, opt\CurrFrameLimit * 99.0, 1) / 99.0
							opt\CurrFrameLimit = Max(opt\CurrFrameLimit, 0.01)
							opt\FrameLimit = 19 + (opt\CurrFrameLimit * 100.0)
						Else
							opt\CurrFrameLimit = 0.0
							opt\FrameLimit = 0
						EndIf
						
						If PrevCurrFrameLimit Then ShouldDeleteGadgets = (PrevCurrFrameLimit <> opt\CurrFrameLimit)
						
						y = y + (80 * MenuScale)
						
						opt\SmoothBars = UpdateMenuTick(x, y, opt\SmoothBars)
						
						y = y + (30 * MenuScale)
						
						opt\PlayStartup = UpdateMenuTick(x, y, opt\PlayStartup)
						
						y = y + (30 * MenuScale)
						
						opt\LauncherEnabled = UpdateMenuTick(x, y, opt\LauncherEnabled)
						
						y = y + (40 * MenuScale)
						
						If UpdateMenuButton(x - (290 * MenuScale), y, 195 * MenuScale, 30 * MenuScale, GetLocalString("options", "reset"))
							DeleteFile(OptionFile)
							ResetOptionsINI()
							SaveOptionsINI(True)
						EndIf
						;[End Block]
				End Select
			EndIf	
			
			;x = 60 * MenuScale
			y = 286 * MenuScale
			
			Width = 400 * MenuScale
			Height = 70 * MenuScale
			x = mo\Viewport_Center_X - (Width / 2)
			
			If mm\MainMenuTab <> MainMenuTab_Options_Audio
				UserTrackCheck = 0
				UserTrackCheck2 = 0
			EndIf
			
			If DelSave = Null And DelCustomMap = Null
				If UpdateMenuButton(x + Width + (20 * MenuScale), y, (580 * MenuScale) - Width - (20 * MenuScale), Height, GetLocalString("menu", "back")) Lor KeyDown(1)
					Select(mm\MainMenuTab)
						Case MainMenuTab_Start_Game
							;[Block]
							mm\MainMenuTab = MainMenuTab_Default
							;[End Block]
						Case MainMenuTab_Story_Mode
							;[Block]
							mm\MainMenuTab = MainMenuTab_Select_Game
							;[End Block]
						Case MainMenuTab_Select_Game, MainMenuTab_Mission_Mode, MainMenuTab_Sandbox_Mode
							;[Block]
							mm\MainMenuTab = MainMenuTab_Start_Game
							;[End Block]
						Case MainMenuTab_New_Game
							;[Block]
							IniWriteString(OptionFile, "Global", "Enable Intro", opt\IntroEnabled)
							For sv.Save = Each Save
								Delete(sv)
							Next
							For cm.CustomMaps = Each CustomMaps
								Delete(cm)
							Next
							mm\MainMenuTab = MainMenuTab_Story_Mode
							;[End Block]
						Case MainMenuTab_Load_Game
							;[Block]
							mm\CurrMenuPage = 0
							For sv.Save = Each Save
								Delete(sv)
							Next
							mm\MainMenuTab = MainMenuTab_Select_Game
							;[End Block]
						Case MainMenuTab_Load_Map
							;[Block]
							mm\CurrMenuPage = 0
							For cm.CustomMaps = Each CustomMaps
								Delete(cm)
							Next
							mm\MainMenuTab = MainMenuTab_New_Game
							;[End Block]
						Case MainMenuTab_Options
							;[Block]
							SaveOptionsINI()
							
							UserTrackCheck = 0
							UserTrackCheck2 = 0
							
							AntiAlias(opt\AntiAliasing)
							TextureLodBias(opt\TextureDetailsLevel)
							TextureAnisotropic(opt\AnisotropicLevel)
							
							mm\MainMenuTab = MainMenuTab_Default
							;[End Block]
						Case MainMenuTab_Options_Graphics, MainMenuTab_Options_Audio, MainMenuTab_Options_Controls, MainMenuTab_Options_Advanced ; ~ Save the options
							;[Block]
							mm\MainMenuTab = MainMenuTab_Options
							;[End Block]
						Case MainMenuTab_Options_Controls_Tab
							;[Block]
							mm\MainMenuTab = MainMenuTab_Options_Controls
							;[End Block]
						Case MainMenuTab_Extras_Achievements
							;[Block]
							mm\MainMenuTab = MainMenuTab_Extras
							;[End Block]
						Default
							;[Block]
							mm\MainMenuTab = MainMenuTab_Default
							;[End Block]
					End Select
					ResetInput()
				EndIf
			Else
				UpdateMenuButton(x + Width + (20 * MenuScale), y, (580 * MenuScale) - Width - (20 * MenuScale), Height, GetLocalString("menu", "back"), Font_Default, False, True)
			EndIf
		EndIf
	Wend
	
	; ~ Go out of function immediately if the game has been start
	If (Not MainMenuOpen) Then Return
	
	If cra\EndingTimer > 2.0
		RenderCredits()
	Else
		RenderMainMenu()
	EndIf
	
	CatchErrors("Uncaught: UpdateMainMenu()")
End Function

Function RenderMainMenu%()
	CatchErrors("RenderMainMenu()")
	
	Local x%, y%, Width%, Height%, Temp%, i%, n%
	Local tX#, tY#, tW#, tH#
	Local TempStr$, TempStr2$
	
	If (Not OnPalette)
		ShowPointer()
	Else
		HidePointer()
	EndIf
	If (MilliSec Mod mm\MainMenuBlinkTimer[0]) >= Rand(mm\MainMenuBlinkDuration[0]) Then DrawImage(mma\BackGround, 0, 0)
	DrawImage(mma\LogoImg, mo\Viewport_Center_X - (580 * MenuScale / 2), mo\Viewport_Center_Y - (1000 * MenuScale / 2))
	SetFontEx(fo\FontID[Font_Default])
	If mm\MainMenuBlinkTimer[1] < mm\MainMenuBlinkDuration[1]
		Color(50, 50, 50)
		TextEx(mm\MainMenuStrX + Rand(-5, 5), mm\MainMenuStrY + Rand(-5, 5), mm\MainMenuStr, True)
		If mm\MainMenuBlinkTimer[1] < 0.0
			mm\MainMenuBlinkTimer[1] = Rnd(700.0, 800.0)
			mm\MainMenuBlinkDuration[1] = Rnd(10.0, 35.0)
			mm\MainMenuStrX = Rand(700, 1000) * MenuScale
			mm\MainMenuStrY = Rand(100, 600) * MenuScale
			
			Select(Rand(0, 23))
				Case 0, 2, 3
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "dontblink")
					;[End Block]
				Case 4, 5
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "scp")
					;[End Block]
				Case 6, 7, 8
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "happyending")
					;[End Block]
				Case 9, 10, 11
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "scream")
					;[End Block]
				Case 12, 19
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "nil")
					;[End Block]
				Case 13
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "menuno")
					;[End Block]
				Case 14
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "bwg")
					;[End Block]
				Case 15
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "173care")
					;[End Block]
				Case 16
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "9341")
					;[End Block]
				Case 17
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "079doors")
					;[End Block]
				Case 18
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "???")
					;[End Block]
				Case 20
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "079king")
					;[End Block]
				Case 21
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "spiral")
					;[End Block]
				Case 22
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "damage")
					;[End Block]
				Case 23
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "howl")
					;[End Block]
			End Select
		EndIf
	EndIf
	SetFontEx(fo\FontID[Font_Default_Big])
	If mm\MainMenuTab <> MainMenuTab_Default
		;x = 60 * MenuScale
		y = 286 * MenuScale
		
		Width = 400 * MenuScale
		Height = 70 * MenuScale
		x = mo\Viewport_Center_X - (Width / 2)
		RenderFrame(x, y, Width, Height)
		
		Color(255, 255, 255)
		SetFontEx(fo\FontID[Font_Default_Big])
		Select(mm\MainMenuTab)
			Case MainMenuTab_Start_Game
				;[Block]
				TempStr = GetLocalString("menu", "stgame")
				;[End Block]
			Case MainMenuTab_Story_Mode, MainMenuTab_Select_Game
				;[Block]
				TempStr = GetLocalString("menu", "strmd")
				;[End Block]
			Case MainMenuTab_Mission_Mode
				;[Block]
				TempStr = GetLocalString("menu", "msnmd")
				;[End Block]
			Case MainMenuTab_Sandbox_Mode
				;[Block]
				TempStr = GetLocalString("menu", "sndbmd")
				;[End Block]
			Case MainMenuTab_New_Game
				;[Block]
				TempStr = GetLocalString("menu", "new")
				;[End Block]
			Case MainMenuTab_Load_Game
				;[Block]
				TempStr = GetLocalString("menu", "load")
				;[End Block]
			Case MainMenuTab_Load_Map
				;[Block]
				TempStr = GetLocalString("menu", "loadmap")
				;[End Block]
			Case MainMenuTab_Options
				;[Block]
				TempStr = GetLocalString("menu", "options")
				;[End Block]
			Case MainMenuTab_Options_Graphics
				;[Block]
				TempStr = GetLocalString("options", "grap")
				;[End Block]
			Case MainMenuTab_Options_Audio
				;[Block]
				TempStr = GetLocalString("options", "audio")
				;[End Block]
			Case MainMenuTab_Options_Controls
				;[Block]
				TempStr = GetLocalString("options", "ctrl")
				;[End Block]
			Case MainMenuTab_Options_Controls_Tab
				;[Block]
				TempStr = GetLocalString("options", "ctrl.tab")
				;[End Block]
			Case MainMenuTab_Options_Advanced
				;[Block]
				TempStr = GetLocalString("options", "avc")
				;[End Block]
			Case MainMenuTab_Extras
				;[Block]
				TempStr = GetLocalString("menu", "extras")
				;[End Block]
			Case MainMenuTab_Extras_Achievements
				;[Block]
				TempStr = GetLocalString("menu", "achv")
				;[End Block]
		End Select
		TextEx(x + (Width / 2), y + (Height / 2), TempStr, True, True)
		
		y = y + Height + (20 * MenuScale)
		
		Width = 580 * MenuScale
		x = mo\Viewport_Center_X - (Width / 2)
		
		If mm\MainMenuTab < MainMenuTab_Options_Graphics
			Select(mm\MainMenuTab)
				Case MainMenuTab_Start_Game
					;[Block]
					Height = 230 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					;[End Block]
				Case MainMenuTab_Story_Mode
					;[Block]
					Height = 200 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					;[End Block]
				Case MainMenuTab_Select_Game
					;[Block]
					Height = 160 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					;[End Block]
				Case MainMenuTab_Mission_Mode
					;[Block]
					Height = 300 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					;[End Block]
				Case MainMenuTab_New_Game
					;[Block]
					Height = 345 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					
					SetFontEx(fo\FontID[Font_Default])
					
					TextEx(x + (20 * MenuScale), y + (25 * MenuScale), GetLocalString("menu", "new.name"))
					
					If SelectedCustomMap = Null
						TempStr = GetLocalString("menu", "new.seed")
					Else
						TempStr = GetLocalString("menu", "new.map")
						RenderFrame(x + (150 * MenuScale), y + (55 * MenuScale), 200 * MenuScale, 30 * MenuScale, True)
						
						Color(255, 0, 0)
						If Len(ConvertToUTF8(SelectedCustomMap\Name)) > 15
							TempStr2 = Left(ConvertToUTF8(SelectedCustomMap\Name), 14) + "..."
						Else
							TempStr2 = ConvertToUTF8(SelectedCustomMap\Name)
						EndIf
						TextEx(x + (250 * MenuScale), y + (70 * MenuScale), TempStr2, True, True)
					EndIf
					Color(255, 255, 255)
					TextEx(x + (20 * MenuScale), y + (65 * MenuScale), TempStr)
					
					TextEx(x + (20 * MenuScale), y + (115 * MenuScale), GetLocalString("menu", "new.intro"))
					
					TextEx(x + (20 * MenuScale), y + (155 * MenuScale), GetLocalString("menu", "new.diff"))
					For i = SAFE To ESOTERIC
						Color(difficulties[i]\R, difficulties[i]\G, difficulties[i]\B)
						TextEx(x + (50 * MenuScale), y + ((185 + 30 * i) * MenuScale), difficulties[i]\Name)
					Next
					
					Color(255, 255, 255)
					RenderFrame(x + (150 * MenuScale), y + (170 * MenuScale), 410 * MenuScale, 160 * MenuScale)
					
					If SelectedDifficulty\Customizable
						; ~ Save type
						Select(SelectedDifficulty\SaveType)
							Case SAVE_ANYWHERE
								;[Block]
								TempStr = GetLocalString("menu", "new.saveany")
								;[End Block]
							Case SAVE_ON_SCREENS
								;[Block]
								TempStr = GetLocalString("menu", "new.savescreen")
								;[End Block]
							Case SAVE_ON_QUIT
								;[Block]
								TempStr = GetLocalString("menu", "new.savequit")
								;[End Block]
							Case NO_SAVES
								;[Block]
								TempStr = GetLocalString("menu", "new.saveno")
								;[End Block]
						End Select
						TextEx(x + (200 * MenuScale), y + (186 * MenuScale), GetLocalString("menu", "new.savetype") + TempStr)
						
						; ~ Aggressive NPCs
						TextEx(x + (200 * MenuScale), y + (215 * MenuScale), GetLocalString("menu", "new.dangernpc"))
						; ~ Realism
						TextEx(x + (200 * MenuScale), y + (246 * MenuScale), Format(GetLocalString("menu", "new.realism"), SelectedDifficulty\Realism))
						
						; ~ Other factor's difficulty
						Select(SelectedDifficulty\OtherFactors)
							Case EASY
								;[Block]
								TempStr = GetLocalString("menu", "new.easy")
								;[End Block]
							Case NORMAL
								;[Block]
								TempStr = GetLocalString("menu", "new.normal")
								;[End Block]
							Case HARD
								;[Block]
								TempStr = GetLocalString("menu", "new.hard")
								;[End Block]
							Case EXTREME
								;[Block]
								TempStr = GetLocalString("menu", "new.extreme")
								;[End Block]
						End Select
						TextEx(x + (200 * MenuScale), y + (276 * MenuScale), Format(GetLocalString("menu", "new.factors"), TempStr))
					Else
						RowText(SelectedDifficulty\Description, x + (160 * MenuScale), y + (180 * MenuScale), 390 * MenuScale, 140 * MenuScale)
						RenderFrame(x + (590 * MenuScale), y + (50 * MenuScale), 340 * MenuScale, 90 * MenuScale)
						Select(SelectedDifficulty\SaveType)
							Case SAVE_ANYWHERE
								;[Block]
								TempStr = GetLocalString("menu", "new.saveany")
								;[End Block]
							Case SAVE_ON_SCREENS
								;[Block]
								TempStr = GetLocalString("menu", "new.savescreen")
								;[End Block]
							Case SAVE_ON_QUIT
								;[Block]
								TempStr = GetLocalString("menu", "new.savequit")
								;[End Block]
							Case NO_SAVES
								;[Block]
								TempStr = GetLocalString("menu", "new.saveno")
								;[End Block]
						End Select
						TextEx(x + (600 * MenuScale), y + (58 * MenuScale), GetLocalString("menu", "new.savetype") + TempStr)
						
						Select(SelectedDifficulty\AggressiveNPCs)
							Case 0
								;[Block]
								TempStr = GetLocalString("menu", "no")
								;[End Block]
							Case 1
								;[Block]
								TempStr = GetLocalString("menu", "yes")
								;[End Block]
						End Select
						TextEx(x + (600 * MenuScale), y + (74 * MenuScale), GetLocalString("menu", "new.dangernpc") + ": "  + TempStr)
						
						Select(SelectedDifficulty\Realism)
							Case 0
								;[Block]
								TempStr = GetLocalString("menu", "no")
								;[End Block]
							Case 1
								;[Block]
								TempStr = GetLocalString("menu", "yes")
								;[End Block]
						End Select
						TextEx(x + (600 * MenuScale), y + (90 * MenuScale), GetLocalString("menu", "new.realism") + ": "  + TempStr)
						
						Select(SelectedDifficulty\OtherFactors)
							Case EASY
								;[Block]
								TempStr = GetLocalString("menu", "new.easy")
								;[End Block]
							Case NORMAL
								;[Block]
								TempStr = GetLocalString("menu", "new.normal")
								;[End Block]
							Case HARD
								;[Block]
								TempStr = GetLocalString("menu", "new.hard")
								;[End Block]
							Case EXTREME
								;[Block]
								TempStr = GetLocalString("menu", "new.extreme")
								;[End Block]
						End Select
						TextEx(x + (600 * MenuScale), y + (106 * MenuScale), Format(GetLocalString("menu", "new.factors"), TempStr))
						
						Select(gm\ID)
							Case GM_RYAN
								;[Block]
								TempStr = GetLocalString("menu", "new.ryan")
								;[End Block]
							Case GM_NTF
								;[Block]
								TempStr = GetLocalString("menu", "new.ntf")
								;[End Block]
							Case GM_CLASS_D
								;[Block]
								TempStr = GetLocalString("menu", "new.classd")
								;[End Block]
							Default
								;[Block]
								TempStr = GetLocalString("menu", "new.unknown")
								;[End Block]
						End Select
						TextEx(x + (600 * MenuScale), y + (122 * MenuScale), Format(GetLocalString("menu", "new.gamemode"), TempStr))	
					EndIf
					
					SetFontEx(fo\FontID[Font_Default_Big])
					;[End Block]
				Case MainMenuTab_Load_Game
					;[Block]
					Height = 430 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					
					y = 376 * MenuScale
					Height = 296 * MenuScale
					
					SetFontEx(fo\FontID[Font_Default_Big])
					
					RenderFrame(x + (60 * MenuScale), y + (440 * MenuScale), Width - (120 * MenuScale), 50 * MenuScale)
					
					TextEx(x + (Width / 2), y + (465 * MenuScale), Format(Format(GetLocalString("menu", "page"), Int(Max((mm\CurrMenuPage + 1), 1)), "{0}"), Int(Max((Int(Ceil(Float(SavedGamesAmount) / 5.0))), 1)), "{1}"), True, True)
					
					SetFontEx(fo\FontID[Font_Default])
					
					If SavedGamesAmount = 0
						RowText(GetLocalString("menu", "save.nosaves"), x + (20 * MenuScale), y + (20 * MenuScale), 540 * MenuScale, 390 * MenuScale)
					Else
						x = x + (20 * MenuScale)
						y = y + (20 * MenuScale)
						
						CurrSave = First Save
						
						For i = 0 To 4 + (5 * mm\CurrMenuPage)
							If i > 0 Then CurrSave = After CurrSave
							If CurrSave = Null Then Exit
							If i >= (5 * mm\CurrMenuPage)
								RenderFrame(x, y, 540 * MenuScale, 70 * MenuScale)
								
								If CurrSave\Version <> VersionNumber
									Color(255, 0, 0)
								Else
									Color(255, 255, 255)
								EndIf
								
								TextEx(x + (10 * MenuScale), y + (10 * MenuScale), ConvertToUTF8(CurrSave\Name))
								TextEx(x + (10 * MenuScale), y + (30 * MenuScale), CurrSave\Time)
								TextEx(x + (110 * MenuScale), y + (30 * MenuScale), CurrSave\Date)
								TextEx(x + (10 * MenuScale), y + (50 * MenuScale), "v" + CurrSave\Version)
								;[Block]
								Local GMStr$
								Select(CurrSave\Gamemode)
									Case GM_RYAN
										;[Block]
										Color(200, 200, 200)
										GMStr = GetLocalString("menu", "new.ryan")
										;[End Block]
									Case GM_NTF
										;[Block]
										Color(0, 10, 200)
										GMStr = GetLocalString("menu", "new.ntf")
										;[End Block]
									Case GM_CLASS_D
										;[Block]
										Color(200, 100, 0)
										GMStr = GetLocalString("menu", "new.classd")
										;[End Block]
								End Select
								;[End Block]
								TextEx(x + (70 * MenuScale), y + (50 * MenuScale), GMStr)
								Color(255, 255, 255)
								;[Block]
								Local ZoneStr$
								Select(CurrSave\Zone)
									Case LCZ_SL_1, LCZ_SL_2, LCZ_SL_3, LCZ
										;[Block]
										Color(120, 200, 255)
										ZoneStr = GetLocalString("zones", "lcz")
										;[End Block]
									Case HCZ_SL_1, HCZ_SL_2, HCZ_SL_3, HCZ
										;[Block]
										Color(255, 0, 0)
										ZoneStr = GetLocalString("zones", "hcz")
										;[End Block]
									Case EZ_SL_1, EZ_SL_2, EZ_SL_3, EZ
										;[Block]
										Color(255, 180, 40)
										ZoneStr = GetLocalString("zones", "ez")
										;[End Block]
									Case RCZ_SL_1, RCZ_SL_2, RCZ_SL_3
										;[Block]
										Color(70, 40, 255)
										ZoneStr = GetLocalString("zones", "rcz")
										;[End Block]
									Case BCZ_SL_1, BCZ_SL_2, BCZ_SL_3
										;[Block]
										Color(100, 255, 120)
										ZoneStr = GetLocalString("zones", "bcz")
										;[End Block]
									Case GATE_A_TOPSIDE, GATE_B_TOPSIDE, GATE_C_TOPSIDE, GATE_D_TOPSIDE
										;[Block]
										Color(170, 255, 255)
										ZoneStr = GetLocalString("zones", "surface")
										;[End Block]
									Default
										;[Block]
										Color(180, 0, 0)
										ZoneStr = "???"
										;[End Block]
								End Select
								;[End Block]
								TextEx(x + (220 * MenuScale), y + (50 * MenuScale), ZoneStr)
								Color(255, 255, 255)
								
								If CurrSave = Last Save Then Exit
								y = y + (80 * MenuScale)
							EndIf
						Next
						
						If DelSave <> Null
							y = 376 * MenuScale
							Width = 100 * MenuScale
							x = mo\Viewport_Center_X - (Width / 2)
							x = x + 340 * MenuScale
							RenderFrame(x, y, 420 * MenuScale, 200 * MenuScale)
							RowText(GetLocalString("menu", "save.delete?"), x + (20 * MenuScale), y + (15 * MenuScale), 400 * MenuScale, 200 * MenuScale)
						EndIf
					EndIf
					;[End Block]
				Case MainMenuTab_Load_Map
					;[Block]
					Height = 430 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					
					y = 376 * MenuScale
					Height = 350 * MenuScale
					
					SetFontEx(fo\FontID[Font_Default_Big])
					
					tX = x + Width
					tY = y
					tW = 400 * MenuScale
					tH = 150 * MenuScale
					
					RenderFrame(x + (60 * MenuScale), y + (440 * MenuScale), Width - (120 * MenuScale), 50 * MenuScale)
					
					TextEx(x + (Width / 2), y + (465 * MenuScale), Format(Format(GetLocalString("menu", "page"), Int(Max((mm\CurrMenuPage + 1), 1)), "{0}"), Int(Max((Int(Ceil(Float(CustomMapsAmount) / 5.0))), 1)), "{1}"), True, True)
					
					SetFontEx(fo\FontID[Font_Default])
					
					If CustomMapsAmount = 0
						RowText(GetLocalString("menu", "nomap"), x + (20 * MenuScale), y + (20 * MenuScale), 540 * MenuScale, 390 * MenuScale)
					Else
						x = x + (20 * MenuScale)
						y = y + (20 * MenuScale)
						
						CurrCustomMap = First CustomMaps
						
						For i = 0 To 4 + (5 * mm\CurrMenuPage)
							If i > 0 Then CurrCustomMap = After CurrCustomMap
							If CurrCustomMap = Null Then Exit
							If i >= (5 * mm\CurrMenuPage)
								RenderFrame(x, y, 540 * MenuScale, 70 * MenuScale)
								
								If Len(ConvertToUTF8(CurrCustomMap\Name)) > 20
									TextEx(x + (20 * MenuScale), y + (15 * MenuScale), Left(ConvertToUTF8(CurrCustomMap\Name), 19) + "...")
								Else
									TextEx(x + (20 * MenuScale), y + (15 * MenuScale), ConvertToUTF8(CurrCustomMap\Name))
								EndIf
								TextEx(x + (20 * MenuScale), y + (45 * MenuScale), ConvertToUTF8(CurrCustomMap\Author))
								
								If MouseOn(x + (280 * MenuScale), y + (20 * MenuScale), 100 * MenuScale, 30 * MenuScale) Lor MouseOn(x + (400 * MenuScale), y + (20 * MenuScale), 100 * MenuScale, 30 * MenuScale)
									RenderMapCreatorTooltip(tX, tY, tW, tH, CurrCustomMap\Name)
								EndIf
								If CurrCustomMap = Last CustomMaps Then Exit
								y = y + (80 * MenuScale)
							EndIf
						Next
						
						If DelCustomMap <> Null
							y = 376 * MenuScale
							Width = 100 * MenuScale
							x = mo\Viewport_Center_X - (Width / 2)
							x = x + 580 * MenuScale
							RenderFrame(x, y, 420 * MenuScale, 200 * MenuScale)
							RowText(GetLocalString("menu", "map.delete?"), x + (20 * MenuScale), y + (15 * MenuScale), 400 * MenuScale, 200 * MenuScale)
						EndIf
					EndIf
					;[End Block]
				Case MainMenuTab_Sandbox_Mode
					;[Block]
					Height = 345 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					
					SetFontEx(fo\FontID[Font_Default])
					
					TempStr = GetLocalString("menu", "new.seed")
					Color(255, 255, 255)
					TextEx(x + (20 * MenuScale), y + (45 * MenuScale), TempStr)
					
					Color(255, 255, 255)
					RenderFrame(x + (150 * MenuScale), y + (170 * MenuScale), 410 * MenuScale, 160 * MenuScale)
					
;					TextEx(x + (200 * MenuScale), y + (186 * MenuScale), GetLocalString("menu", "new.savetype") + TempStr)
;					
;					TextEx(x + (200 * MenuScale), y + (215 * MenuScale), GetLocalString("menu", "new.dangernpc"))
;					
;					TextEx(x + (200 * MenuScale), y + (246 * MenuScale), Format(GetLocalString("menu", "new.rea;ism"), SelectedDifficulty\Realism))
					
					SetFontEx(fo\FontID[Font_Default_Big])
					;[End Block]
				Case MainMenuTab_Options
					;[Block]
					Height = 300 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					;[End Block]
				Case MainMenuTab_Extras
					;[Block]
					Height = 300 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					;[End Block]
			End Select
		Else
			x = x + (20 * MenuScale)
			
			tX = x - (20 * MenuScale) + Width
			tY = y
			tW = 400.0 * MenuScale
			tH = 150.0 * MenuScale
			
			Select(mm\MainMenuTab)
				Case MainMenuTab_Options_Graphics
					;[Block]
					Height = 485 * MenuScale
					RenderFrame(x - (20 * MenuScale), y, Width, Height)
					
					y = y + (20 * MenuScale)
					
					SetFontEx(fo\FontID[Font_Default])
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "bump"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_BumpMapping)
					
					y = y + (30 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "vsync"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_VSync)
					
					y = y + (30 * MenuScale)
					
					Color(255 - (155 * (opt\DisplayMode <> 0)), 255 - (155 * (opt\DisplayMode <> 0)), 255 - (155 * (opt\DisplayMode <> 0)))
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "antialias"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AntiAliasing)
					
					y = y + (30 * MenuScale)
					
					Color(255, 255, 255)
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "lights"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_RoomLights)
					
					y = y + (40 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "gamma"))
					If (MouseOn(x + (290 * MenuScale), y, 164 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ScreenGamma, opt\ScreenGamma)
					
					y = y + (45 * MenuScale)
					
					TextEx(x, y, GetLocalString("options", "particle"))
					If (MouseOn(x + (290 * MenuScale), y - (8 * MenuScale), 164 * MenuScale, 18 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 2 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ParticleAmount, opt\ParticleAmount)
					
					y = y + (45 * MenuScale)
					
					TextEx(x, y, GetLocalString("options", "lod"))
					If (MouseOn(x + (290 * MenuScale), y - (8 * MenuScale), 164 * MenuScale, 18 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 3 Then RenderOptionsTooltip(tX, tY, tW, tH + (100 * MenuScale), Tooltip_TextureLODBias)
					
					y = y + (35 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "vram"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SaveTexturesInVRAM)
					
					y = y + (40 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "fov"))
					If (MouseOn(x + (290 * MenuScale), y, 164 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 4 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FOV)
					
					y = y + (45 * MenuScale)
					
					TextEx(x, y, GetLocalString("options", "filter"))
					If (MouseOn(x + (290 * MenuScale), y - (8 * MenuScale), 164 * MenuScale, 18 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 5 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AnisotropicFiltering)
					
					y = y + (35 * MenuScale)
					
					If opt\Atmosphere
						TempStr = GetLocalString("options", "atmo.bright")
					Else
						TempStr = GetLocalString("options", "atmo.dark")
					EndIf
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "atmo") + TempStr)
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Atmosphere)
					
					y = y + (45 * MenuScale)
					
					TextEx(x, y, GetLocalString("options", "screnderinterval"))
					If (MouseOn(x + (290 * MenuScale), y - (8 * MenuScale), 164 * MenuScale, 18 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 17 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SecurityCamRenderInterval)
					;[End Block]
				Case MainMenuTab_Options_Audio
					;[Block]
					Height = ((280 + (70 * opt\EnableUserTracks)) + (30 * opt\EnableSubtitles) + (155 * (opt\EnableSubtitles And opt\OverrideSubColor))) * MenuScale
					RenderFrame(x - (20 * MenuScale), y, Width, Height)
					
					y = y + (20 * MenuScale)
					
					SetFontEx(fo\FontID[Font_Default])
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "mastervolume"))
					If (MouseOn(x + (290 * MenuScale), y, 164 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MasterVolume, opt\PrevMasterVolume)
					
					y = y + (40 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "musicvolume"))
					If (MouseOn(x + (290 * MenuScale), y, 164 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 2 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MusicVolume, opt\MusicVolume)
					
					y = y + (40 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "soundvolume"))
					If (MouseOn(x + (290 * MenuScale), y, 164 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 3 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SoundVolume, opt\SFXVolume)
					
					y = y + (40 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "voicevolume"))
					If (MouseOn(x + (290 * MenuScale), y, 164 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 18 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_VoiceVolume, opt\VoiceVolume)
					
					y = y + (40 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "autorelease"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH + (220 * MenuScale), Tooltip_SoundAutoRelease)
					
					y = y + (30 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "enabletracks"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTracks)
					
					If opt\EnableUserTracks
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "trackmode"))
						If opt\UserTrackMode
							TempStr = GetLocalString("options", "track.repeat")
						Else
							TempStr = GetLocalString("options", "track.random")
						EndIf
						TextEx(x + (330 * MenuScale), y + (5 * MenuScale), TempStr)
						If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTracksMode)
						If MouseOn(x, y + (30 * MenuScale), 210 * MenuScale, 30 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTrackScan)
						If UserTrackCheck > 0 Then TextEx(x, y + (100 * MenuScale), Format(Format(GetLocalString("options", "track.found"), UserTrackCheck2, "{0}"), UserTrackCheck, "{1}"))
						
						y = y + (40 * MenuScale)
					EndIf
					
					y = y + (30 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "subtitles"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Subtitles)
					
					If opt\EnableSubtitles
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "subtitles.color"))
						
						y = y + (5 * MenuScale)
						
						If MouseOn(x + (230 * MenuScale), y, 147 * MenuScale, 147 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SubtitlesColor)
						
						If opt\OverrideSubColor
							y = y + (60 * MenuScale)
							
							TextEx(x, y + (5 * MenuScale), GetLocalString("options", "subtitles.color.red"))
							If MouseOn(x + (125 * MenuScale), y, 40 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SubtitlesColor)
							
							y = y + (30 * MenuScale)
							
							TextEx(x, y + (5 * MenuScale), GetLocalString("options", "subtitles.color.green"))
							If MouseOn(x + (125 * MenuScale), y, 40 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SubtitlesColor)
							
							y = y + (30 * MenuScale)
							
							TextEx(x, y + (5 * MenuScale), GetLocalString("options", "subtitles.color.blue"))
							If MouseOn(x + (125 * MenuScale), y, 40 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SubtitlesColor)
							
							Color(20, 20, 20)
							Rect(x - (20 * MenuScale), Height + (390 * MenuScale), Width, 20 * MenuScale)
							Color(opt\SubColorR, opt\SubColorG, opt\SubColorB)
							TextEx(x, Height + (396 * MenuScale), GetLocalString("options", "subtitles.example"))
						EndIf
					EndIf
					;[End Block]
				Case MainMenuTab_Options_Controls
					;[Block]
					Height = 220 * MenuScale
					RenderFrame(x - (20 * MenuScale), y, Width, Height)
					
					y = y + (20 * MenuScale)
					
					SetFontEx(fo\FontID[Font_Default])
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "mousesensitive"))
					If (MouseOn(x + (290 * MenuScale), y, 164 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseSensitivity, opt\MouseSensitivity)
					
					y = y + (40 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "invertx"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseInvertX)
					
					y = y + (40 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "inverty"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseInvertY)
					
					y = y + (40 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "mousesmooth"))
					If (MouseOn(x + (290 * MenuScale), y, 164 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 2 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseSmoothing, opt\MouseSmoothing)
					
					y = y + (40 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("menu", "controlconfig"))
					
					y = y + (40 * MenuScale)
					
					If MouseOn(x, y, 200 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ControlConfiguration)
					;[End Block]
				Case MainMenuTab_Options_Controls_Tab
					;[Block]
					Height = 240 * MenuScale
					RenderFrame(x - (20 * MenuScale), y, Width, Height)
					SetFontEx(fo\FontID[Font_Default])
					
					y = y + (20 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.forward"))
					TextEx(x + (260 * MenuScale), y, GetLocalString("options", "key.reload"))
					
					y = y + (20 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.left"))
					TextEx(x + (260 * MenuScale), y, GetLocalString("options", "key.checkammo"))
					
					y = y + (20 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.backward"))
					TextEx(x + (260 * MenuScale), y, GetLocalString("options", "key.inspect"))
					
					y = y + (20 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.right"))
					TextEx(x + (260 * MenuScale), y, GetLocalString("options", "key.firemode"))
					
					y = y + (20 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.interact"))
					TextEx(x + (260 * MenuScale), y, GetLocalString("options", "key.lower"))
					
					y = y + (20 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.sprint"))
					TextEx(x + (260 * MenuScale), y, GetLocalString("options", "key.holster"))
					
					y = y + (20 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.crouch"))
					TextEx(x + (260 * MenuScale), y, GetLocalString("options", "key.save"))
					
					y = y + (20 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.blink"))
					TextEx(x + (260 * MenuScale), y, GetLocalString("options", "key.load"))
					
					y = y + (20 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.inv"))
					TextEx(x + (260 * MenuScale), y, GetLocalString("options", "key.screenshot"))
					
					y = y + (20 * MenuScale)
					
					If opt\CanOpenConsole Then TextEx(x + (260 * MenuScale), y, GetLocalString("options", "key.console"))
					;[End Block]
				Case MainMenuTab_Options_Advanced
					;[Block]
					Height = 460 * MenuScale
					RenderFrame(x - (20 * MenuScale), y, Width, Height)
					
					y = y + (20 * MenuScale)
					
					SetFontEx(fo\FontID[Font_Default])
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "hud"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_HUD)
					
					y = y + (30 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "console"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Console)
					
					y = y + (30 * MenuScale)
					
					If opt\CanOpenConsole
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "error"))
						If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ConsoleOnError)
					EndIf
					
					y = y + (30 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "achipop"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AchievementPopups)
					
					y = y + (30 * MenuScale)
					
					Color(255 - (155 * (SelectedDifficulty\SaveType <> SAVE_ANYWHERE)), 255 - (155 * (SelectedDifficulty\SaveType <> SAVE_ANYWHERE)), 255 - (155 * (SelectedDifficulty\SaveType <> SAVE_ANYWHERE)))
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "save"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AutoSave)
					
					y = y + (30 * MenuScale)
					
					Color(255, 255, 255)
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "txtshadow"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_TextShadow)
					
					y = y + (30 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "fps"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FPS)
					
					y = y + (30 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "frame"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FrameLimit, opt\FrameLimit)
					If opt\CurrFrameLimit > 0.0
						Color(255, 255, 0)
						TextEx(x, y + (45 * MenuScale), opt\FrameLimit + " FPS")
						If (MouseOn(x + (130 * MenuScale), y + (40 * MenuScale), 164 * MenuScale, 20 * MenuScale) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FrameLimit, opt\FrameLimit)
					EndIf
					
					y = y + (80 * MenuScale)
					
					Color(255, 255, 255)
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "bar"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SmoothBars)
					
					y = y + (30 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "startvideo"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_StartupVideos)
					
					y = y + (30 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "launcher"))
					If MouseOn(x + (290 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Launcher)
					
					y = y + (40 * MenuScale)
					
					If MouseOn(x, y, 195 * MenuScale, 30 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ResetOptions)
					;[End Block]
			End Select
		EndIf
	EndIf
	
	RenderMenuButtons()
	RenderMenuPalettes()
	RenderMenuTicks()
	RenderMenuInputBoxes()
	RenderMenuSlideBars()
	RenderMenuSliders()
	RenderMenuScrollBars()
	
	y = y + Height + (20 * MenuScale)
	
	Width = 580 * MenuScale
	x = mo\Viewport_Center_X - (Width / 2)
	
	Select(mm\MainMenuTab)
		Case MainMenuTab_Story_Mode
			;[Block]
			Height = 160 * MenuScale
			
			DrawImage(mma\StoryIMG[Menu_Ryan], x + (24 * MenuScale), y - Height - (40 * MenuScale))
			DrawImage(mma\StoryIMG[Menu_NTF], x + (48 * MenuScale) + Height, y - Height - (40 * MenuScale))
			DrawImage(mma\StoryIMG[Menu_Class_D], x + (73 * MenuScale) + (Height * 2), y - Height - (40 * MenuScale))
			;[End Block]
	End Select
	
	If opt\HUDEnabled
		Color(255, 255, 255)
		SetFontEx(fo\FontID[Font_Console])
		TextEx(20 * MenuScale, opt\GraphicHeight - (90 * MenuScale), "SCP: Classified Stories v" + VersionNumber)
		Color(0, 170, 255)
		TextEx(20 * MenuScale, opt\GraphicHeight - (70 * MenuScale), "Made By Northern Wolf Industries")
		If MouseOn(20 * MenuScale, opt\GraphicHeight - (50 * MenuScale), 180 * MenuScale, 10 * MenuScale)
			Color(0, 180, 200)
			If mo\MouseHit1 Then mm\PressedSecret = MenuSecret_Wolfnaya
		Else
			Color(255, 0, 0)
		EndIf
		TextEx(20 * MenuScale, opt\GraphicHeight - (50 * MenuScale), "Founded by Wolfnaya")
		Color(255, 255, 255)
		If opt\ShowFPS
			SetFontEx(fo\FontID[Font_Console])
			TextEx(20 * MenuScale, opt\GraphicHeight - (30 * MenuScale), "FPS: " + fps\FPS)
		EndIf
	EndIf
	
	RenderConsole()
	
	RenderCursor()
	
	SetFontEx(fo\FontID[Font_Default])
	
	CatchErrors("Uncaught: RenderMainMenu()")
End Function

Function RenderCursor%()
	If opt\DisplayMode <> 0 Lor OnPalette Then Return
	DrawImage(CursorIMG, MousePosX, MousePosY)
End Function

Global TextR# = 0.0, TextG# = 0.0, TextB# = 0.0
Global ChangeColor%

Function RenderLoadingText%(x%, y%, Txt$, AlignX% = False, AlignY% = False)
	If TextR = 0.0
		ChangeColor = True
	ElseIf TextR = 255.0
		ChangeColor = False
	EndIf
	
	If (Not ChangeColor)
		TextR = Max(0.0, TextR - 3.0)
		TextG = Max(0.0, TextG - 3.0)
		TextB = Max(0.0, TextB - 3.0)
	Else
		TextR = Min(TextR + 3.0, 255.0)
		TextG = Min(TextG + 3.0, 255.0)
		TextB = Min(TextB + 3.0, 255.0)
	EndIf
	SetFontEx(fo\FontID[Font_Default])
	Color(TextR, TextG, TextB)
	TextEx(x, y, Txt, AlignX, AlignY)
End Function

Function ResetLoadingTextColor%()
	TextR = 0.0 : TextG = 0.0 : TextB = 0.0
End Function

Global LoadingScreens%
Global LoadingBack%, LoadingImage%
Global SelectedLoadingScreens%, LoadingScreenTitle$
Global Descriptions%, DescriptionIndex%
Global ImageAlignX$, ImageAlignY$
Global CWMText$

Function RenderLoading%(Percent%, Assets$ = "")
	CatchErrors("RenderLoading(" + Percent + ", " + Assets + ")")
	
	Local x%, y%, FirstLoop%
	
	HidePointer()
	
	If Percent = 0
		If LoadingImage = 0
			DescriptionIndex = 0
			
			SelectedLoadingScreens = JsonGetArrayValue(LoadingScreens, Rand(0, JsonGetArraySize(LoadingScreens) - 1))
			LoadingScreenTitle = JsonGetString(JsonGetValue(SelectedLoadingScreens, "title"))
			If JsonIsNull(JsonGetValue(SelectedLoadingScreens, "descriptions"))
				Descriptions = JsonGetArray(JsonParseFromString("[" + Chr(34) + Chr(34) + "]")) ; ~ Create an empty description array
			Else
				Descriptions = JsonGetArray(JsonGetValue(SelectedLoadingScreens, "descriptions"))
			EndIf
			ImageAlignX = JsonGetString(JsonGetValue(SelectedLoadingScreens, "align_x"))
			ImageAlignY = JsonGetString(JsonGetValue(SelectedLoadingScreens, "align_y"))
			LoadingImage = LoadImage_Strict("LoadingScreens\" + JsonGetString(JsonGetValue(SelectedLoadingScreens, "image")))
			LoadingImage = ScaleImage2(LoadingImage, MenuScale, MenuScale)
			If JsonGetBool(JsonGetValue(SelectedLoadingScreens, "background"))
				If LoadingBack = 0
					LoadingBack = LoadImage_Strict("LoadingScreens\loading_back.png")
					LoadingBack = ScaleImage2(LoadingBack, MenuScale, MenuScale)
				EndIf
			EndIf
		EndIf
	EndIf
	
	FirstLoop = True
	
	Repeat 
		ClsColor(0, 0, 0)
		Cls()
		
		If Percent > 20 Then UpdateMusic()
		If Percent > (100.0 / JsonGetArraySize(Descriptions)) * (DescriptionIndex + 1) Then DescriptionIndex = DescriptionIndex + 1
		
		If LoadingBack <> 0 Then DrawBlock(LoadingBack, mo\Viewport_Center_X - ImageWidth(LoadingBack) / 2, mo\Viewport_Center_Y - ImageHeight(LoadingBack) / 2)
		
		If ImageAlignX = "center"
			x = mo\Viewport_Center_X - ImageWidth(LoadingImage) / 2
		ElseIf ImageAlignX = "right"
			x = opt\GraphicWidth - ImageWidth(LoadingImage)
		Else ; ~ ImageAlignX = "left"
			x = 0
		EndIf
		
		If ImageAlignY = "center"
			y = mo\Viewport_Center_Y - ImageHeight(LoadingImage) / 2
		ElseIf ImageAlignY = "bottom"
			y = opt\GraphicHeight - ImageHeight(LoadingImage)
		Else ; ~ ImageAlignY = "top"
			y = 0
		EndIf
		
		DrawImage(LoadingImage, x, y)
		
		Local Width% = 300 * MenuScale
		Local Height% = 20 * MenuScale
		Local i%
		
		x = mo\Viewport_Center_X - (Width / 2)
		y = opt\GraphicHeight - (80 * MenuScale)
		
		RenderBar(x, y, Width, Height, Percent, 100.0, 200, 200, 200)
		
		Color(255, 255, 255)
		SetFontEx(fo\FontID[Font_Default])
		TextEx(x + (Width / 2), opt\GraphicHeight - (70 * MenuScale), Percent + "%", True, True)
		
		If LoadingScreenTitle = "CWM"
			If FirstLoop
				If Percent = 0
					PlaySound_Strict(LoadTempSound("SFX\SCP\990\cwm1.cwm"))
				ElseIf Percent = 100
					PlaySound_Strict(LoadTempSound("SFX\SCP\990\cwm2.cwm"))
				EndIf
			EndIf
			
			Local StrTemp$ = ""
			Local Temp% = Rand(2, 9)
			
			For i = 0 To Temp
				StrTemp = StrTemp + Chr(Rand(48, 122))
			Next
			SetFontEx(fo\FontID[Font_Default_Big])
			TextEx(mo\Viewport_Center_X, mo\Viewport_Center_Y - (450 * MenuScale), StrTemp, True, True)
			
			If Percent = 0
				If Rand(5) = 1
					Select(Rand(2))
						Case 1
							;[Block]
							CWMText = Format(GetLocalString("menu", "happend"), CurrentTime())
							;[End Block]
						Case 2
							;[Block]
							CWMText = CurrentTime()
							;[End Block]
					End Select
				Else
					Select(Rand(13))
						Case 1
							;[Block]
							CWMText = GetLocalString("menu", "990_1")
							;[End Block]
						Case 2
							;[Block]
							CWMText = GetLocalString("menu", "990_2")
							;[End Block]
						Case 3
							;[Block]
							CWMText = GetLocalString("menu", "990_3")
							;[End Block]
						Case 4
							;[Block]
							CWMText = "eof9nsd3jue4iwe1fgj"
						Case 5
							;[Block]
							CWMText = GetLocalString("menu", "990_4")
							;[End Block]
						Case 6 
							;[Block]
							CWMText = GetLocalString("menu", "990_5")
							;[End Block]
						Case 7
							;[Block]
							CWMText = "???____??_???__????n?"
							;[End Block]
						Case 8, 9
							;[Block]
							CWMText = GetLocalString("menu", "990_6")
							;[End Block]
						Case 10
							;[Block]
							CWMText = "???????????"
							;[End Block]
						Case 11
							;[Block]
							CWMText = GetLocalString("menu", "990_7")
							;[End Block]
						Case 12
							;[Block]
							CWMText = GetLocalString("menu", "990_8")
							;[End Block]
						Case 13
							;[Block]
							CWMText = GetLocalString("menu", "990_9")
							;[End Block]
					End Select
				EndIf
			EndIf
			
			StrTemp = CWMText
			Temp = Int(Len(CWMText) - Rand(5))
			For i = 0 To Rand(10, 15)
				StrTemp = Replace(CWMText, Mid(CWMText, Rand(Len(StrTemp) - 1), 1), Chr(Rand(130, 250)))
			Next
			SetFontEx(fo\FontID[Font_Default])
			RowText(StrTemp, mo\Viewport_Center_X - (200 * MenuScale), mo\Viewport_Center_Y + (250 * MenuScale), 400 * MenuScale, 300 * MenuScale, True)
		Else
			Color(255, 255, 255)
			SetFontEx(fo\FontID[Font_Default_Big])
			TextEx(mo\Viewport_Center_X, mo\Viewport_Center_Y - (450 * MenuScale), LoadingScreenTitle, True, True)
			SetFontEx(fo\FontID[Font_Default])
			RowText(JsonGetString(JsonGetArrayValue(Descriptions, DescriptionIndex)), mo\Viewport_Center_X - (200 * MenuScale), mo\Viewport_Center_Y + (250 * MenuScale), 400 * MenuScale, 300 * MenuScale, True)
		EndIf
		
		If Percent <> 100
			Color(255, 255, 255)
			TextEx(mo\Viewport_Center_X, opt\GraphicHeight - (35 * MenuScale), Format(GetLocalString("loading", "assets"), Assets), True, True)
			
			ResetInput()
		Else
			If LoadingScreenTitle = "CWM"
				StrTemp = GetLocalString("menu", "wakeup")
			Else
				If FirstLoop Then PlaySound_Strict(LoadTempSound(("SFX\Horror\Horror8.ogg")))
				StrTemp = GetLocalString("menu", "anykey")
			EndIf
			RenderLoadingText(mo\Viewport_Center_X, opt\GraphicHeight - (35 * MenuScale), StrTemp, True, True)
		EndIf
		
		RenderGamma()
		
		Flip(True)
		
		FirstLoop = False
		If Percent <> 100 Then Exit
		
		Local Close% = False
		
		If GetAnyKey();GetKey() <> 0 Lor MouseHit(1)
			ResetLoadingTextColor()
			ResetInput()
			ResetTimingAccumulator()
			SetFontEx(fo\FontID[Font_Default])
			Close = True
			DeleteMenuGadgets()
			FreeImage(LoadingImage) : LoadingImage = 0
			FreeImage(LoadingBack) : LoadingBack = 0
			SelectedLoadingScreens = 0
		EndIf
	Until Close
	
	CatchErrors("Uncaught: RenderLoading(" + Percent + ", " + Assets + ")")
End Function

Function RenderTiledImageRect%(Img%, SrcX%, SrcY%, SrcWidth%, SrcHeight%, x%, y%, Width%, Height%)
	Local TempSrcWidth%, TempSrcHeight%
	Local x2% = x
	
    While x2 < x + Width
        TempSrcWidth = SrcWidth
        If x2 + SrcWidth > x + Width Then TempSrcWidth = (x + Width) - x2
        
        Local y2% = y
		
        While y2 < y + Height
            TempSrcHeight = SrcHeight
            If y2 + SrcHeight > y + Height Then TempSrcHeight = (y + Height) - y2
            
            DrawBlockRect(Img, x2, y2, SrcX, SrcY, TempSrcWidth, TempSrcHeight)
            y2 = y2 + TempSrcHeight
        Wend
        x2 = x2 + TempSrcWidth
    Wend
End Function

Const FRAME_THICK = 3

Function RenderFrame%(x%, y%, Width%, Height%, Locked% = False, RedIn% = 10.0, GreenIn% = 10.0, BlueIn% = 10.0, RedOut% = 50.0, GreenOut% = 50.0, BlueOut% = 50.0)
	If Locked
		Color(150.0, 150.0, 150.0)
	Else
		Color(RedIn, GreenIn, BlueIn)
	EndIf
	Rect(x, y, Width, Height)
	Color(RedOut, GreenOut, BlueOut)
	Rect(x + (3 * MenuScale), y + (3 * MenuScale), Width - (6 * MenuScale), Height - (6 * MenuScale), False)
	Color(255.0, 255.0, 255.0)
End Function

Function RenderBar%(x%, y%, Width%, Height%, Value1#, Value2# = 100.0, R% = 100, G% = 100, B% = 100)
	Local i%
	
	Rect(x, y, Width + (4 * MenuScale), Height, False)
	If opt\SmoothBars
		Color(R, G, B)
		Rect(x + (3 * MenuScale), y + (3 * MenuScale), Float((Width - (2 * MenuScale)) * (Value1 / Value2)), Height - (6 * MenuScale))
	Else
		For i = 1 To Int(((Width - (2 * MenuScale)) * ((Value1 / Value2) / 10.0)) / MenuScale)
			Rect(x + ((3 + (10 * (i - 1))) * MenuScale), y + (3 * MenuScale), 8, 14)
		Next
	EndIf
End Function

Type MenuButton
	Field x%, y%, Width%, Height%
	Field Txt$
	Field FontID%
	Field Locked%
	Field R%, G%, B%
End Type

Function UpdateMenuButton%(x%, y%, Width%, Height%, Txt$, FontID% = Font_Default, WaitForMouseUp% = False, Locked% = False, R% = 255, G% = 255, B% = 255)
	Local mb.MenuButton, currMButton.MenuButton
	Local Clicked% = False
	Local ButtonExists% = False
	
	For mb.MenuButton = Each MenuButton
		If mb\x = x And mb\y = y And mb\Width = Width And mb\Height = Height
			ButtonExists = True
			Exit
		EndIf
	Next
	If (Not ButtonExists)
		mb.MenuButton = New MenuButton
		mb\x = x
		mb\y = y
		mb\Width = Width
		mb\Height = Height
		mb\Txt = Txt
		mb\FontID = FontID
		mb\Locked = Locked
		mb\R = R
		mb\B = B
		mb\G = G
	Else
		currMButton = mb
		currMButton\Txt = Txt
		currMButton\FontID = FontID
		currMButton\Locked = Locked
	EndIf
	
	If MouseOn(x, y, Width, Height)
		If (mo\MouseHit1 And (Not WaitForMouseUp)) Lor (mo\MouseUp1 And WaitForMouseUp)
			If Locked
				PlaySound_Strict(ButtonSFX[1])
			Else
				Clicked = True
				PlaySound_Strict(ButtonSFX[0])
			EndIf
		EndIf
	EndIf
	Return(Clicked)
End Function

Function RenderMenuButtons%()
	Local mb.MenuButton
	
	For mb.MenuButton = Each MenuButton
		RenderFrame(mb\x, mb\y, mb\Width, mb\Height, mb\Locked)
		If MouseOn(mb\x, mb\y, mb\Width, mb\Height)
			Color(30, 30, 30)
			Rect(mb\x + (3 * MenuScale), mb\y + (3 * MenuScale), mb\Width - (6 * MenuScale), mb\Height - (6 * MenuScale))
		Else
			Color(0, 0, 0)
		EndIf
		
		If mb\Locked
			If mb\R <> 255 Lor mb\G <> 255 Lor mb\B <> 255
				Color(mb\R, mb\G, mb\B)
			Else
				Color(100, 100, 100)
			EndIf
		Else
			Color(mb\R, mb\G, mb\B)
		EndIf
		SetFontEx(fo\FontID[mb\FontID])
		TextEx(mb\x + (mb\Width / 2), mb\y + (mb\Height / 2), mb\Txt, True, True)
	Next
End Function

Function DeleteMenuButton%(mb.MenuButton)
	Delete(mb)
End Function

Type MenuTick
	Field x%, y%
	Field Selected%
	Field Locked%
End Type

Function UpdateMenuTick%(x%, y%, Selected%, Locked% = False)
	Local mt.MenuTick, currTick.MenuTick
	Local TickExists% = False
	Local Width% = 20 * MenuScale
	Local Height% = 20 * MenuScale
	
	For mt.MenuTick = Each MenuTick
		If mt\x = x And mt\y = y
			TickExists = True
			Exit
		EndIf
	Next
	If (Not TickExists)
		mt.MenuTick = New MenuTick
		mt\x = x
		mt\y = y
		mt\Selected = Selected
		mt\Locked = Locked
	Else
		currTick = mt
		currTick\Selected = Selected
		currTick\Locked = Locked
	EndIf
	
	Local Highlight% = MouseOn(x, y, Width, Height)
	
	If Highlight
		If mo\MouseHit1
			If Locked
				PlaySound_Strict(ButtonSFX[1])
			Else
				Selected = (Not Selected)
				PlaySound_Strict(ButtonSFX[0])
			EndIf
		EndIf
	EndIf
	Return(Selected)
End Function

Function RenderMenuTicks%()
	Local mt.MenuTick
	Local Width% = 20 * MenuScale
	Local Height% = 20 * MenuScale
	Local CLR%
	
	For mt.MenuTick = Each MenuTick
		
		If mt\Locked
			CLR = 20
		Else
			CLR = 255
		EndIf
		
		Color(CLR, CLR, CLR)
		Rect(mt\x, mt\y, Width, Height)
		;RenderTiledImageRect(IMG, (mt\x Mod 256), (mt\y Mod 256), 512, 512, mt\x, mt\y, Width, Height)
		
		Local Highlight% = MouseOn(mt\x, mt\y, Width, Height)
		Local ColorR% = 0, ColorG% = 0, ColorB% = 0
		
		If Highlight Then ColorR = 50 : ColorG = 50 : ColorB = 50
		Color(ColorR, ColorG, ColorB)
		
		Rect(mt\x + 2, mt\y + 2, Width - 4, Height - 4)
		
		If mt\Selected
			If Highlight
				ColorR = ColorR * 5.1 : ColorG = ColorG * 5.1 : ColorB = ColorB * 5.1
			Else
				ColorR = ColorR + 200 : ColorG = ColorG + 200 : ColorB = ColorB + 200
			EndIf
			Color(ColorR, ColorG, ColorB)
			Rect(mt\x + 4, mt\y + 4, Width - 8, Height - 8)
			;RenderTiledImageRect(IMG, (mt\x Mod 256), (mt\y Mod 256), 512, 512, mt\x + 4, mt\y + 4, Width - 8, Height - 8)
		EndIf
		Color(255, 255, 255)
	Next
End Function

Function DeleteMenuTick%(mt.MenuTick)
	Delete(mt)
End Function

Type MenuPalette
	Field Img%
	Field x%, y%, Width%, Height%
End Type

Global OnPalette%

Function UpdateMenuPalette%(x%, y%)
	Local mp.MenuPalette
	Local PaletteExists% = False
	
	For mp.MenuPalette = Each MenuPalette
		If mp\x = x And mp\y = y
			PaletteExists = True
			Exit
		EndIf
	Next
	If (Not PaletteExists)
		mp.MenuPalette = New MenuPalette
		If mp\Img = 0
			mp\Img = LoadImage_Strict("GFX\Menu\Images\Palette.png")
			mp\Img = ScaleImage2(mp\Img, MenuScale, MenuScale)
		EndIf
		mp\x = x
		mp\y = y
		mp\Width = ImageWidth(mp\Img)
		mp\Height = ImageHeight(mp\Img)
	EndIf
	
	OnPalette = (MouseOn(x, y, mp\Width, mp\Height))
End Function

Function RenderMenuPalettes%()
	Local mp.MenuPalette
	Local CoordEx% = 5 * MenuScale
	
	For mp.MenuPalette = Each MenuPalette
		DrawImage(mp\Img, mp\x, mp\y)
		If MouseOn(mp\x, mp\y, mp\Width, mp\Height)
			If mo\MouseDown1 And OnSliderID = 0
				LockBuffer(BackBuffer())
				
				Local Pixel% = ReadPixelFast(MousePosX, MousePosY, BackBuffer())
				
				UnlockBuffer(BackBuffer())
				opt\SubColorR = ReadPixelColor(Pixel, 16)
				opt\SubColorG = ReadPixelColor(Pixel, 8)
				opt\SubColorB = ReadPixelColor(Pixel, 0)
			EndIf
			Color(0, 0, 0)
			Oval(MousePosX, MousePosY, CoordEx, CoordEx, False)
		EndIf
	Next
End Function

Function DeleteMenuPallete%(mp.MenuPalette)
	If mp\Img <> 0 Then FreeImage(mp\Img) : mp\Img = 0
	Delete(mp)
End Function

Function ChrCanDisplay%(Char%)
	Return((Char >= 32) And (Char <= 126))
End Function

Global PrevInputBoxCtrl%, InsertMode% = False

Function UpdateInput$(aString$, MaxChr%)
	Local Value% = GetKey()
	Local Length% = Len(aString)
	
	If (CursorPos < 0) And (CursorPos <> -1) Then CursorPos = Length
	If CursorPos < 0 Then CursorPos = 0
	
	If KeyHit(210) Then InsertMode = (Not InsertMode) ; ~ Insert key
	If KeyHit(199) Then CursorPos = 0 ; ~ Home key
	If KeyHit(207) Then CursorPos = Length ; ~ End key
	If KeyHit(211) ; ~ Delete key
		aString = Left(aString, CursorPos) + Right(aString, Max(Length - CursorPos - 1, 0.0))
		CursorPos = CursorPos + 1
	EndIf
	
	If KeyDown(29) Lor KeyDown(157) ; ~ Control key
		If Value = 30 Then CursorPos = Length ; ~ Control & Right arrow
		If Value = 31 Then CursorPos = 0 ; ~ Control & Left arrow
		If Value = 3 Then SetClipboardContents(aString) ; ~ Control & C
		If Value = 22 ; ~ Control & V
			aString = Left(aString, CursorPos) + GetClipboardContents() + Right(aString, Length - CursorPos)
			CursorPos = CursorPos + Len(aString) - Length
			If MaxChr > 0 And MaxChr < Len(aString) Then aString = Left(aString, MaxChr) : CursorPos = MaxChr
		EndIf
		Return(aString)
	EndIf
	
	If Value = 30 Then
		CursorPos = CursorPos + 1
		PrevInputBoxCtrl = MilliSecs()
		Return(aString)
	EndIf
	If Value = 31 Then
		CursorPos = CursorPos - 1
		PrevInputBoxCtrl = MilliSecs()
		Return(aString)
	EndIf
	
	If KeyDown(205) And ((MilliSecs() - PrevInputBoxCtrl) > 500) ; ~ Right arrow
		If (MilliSecs() Mod 100) < 25 Then CursorPos = Min(CursorPos + 1, Length)
	ElseIf KeyDown(203) And ((MilliSec - PrevInputBoxCtrl) > 500) ; ~ Left arrow
		If (MilliSecs() Mod 100) < 25 Then CursorPos = Max(CursorPos - 1, 0.0)
	Else
		If InsertMode
			If ChrCanDisplay(Value)
				aString = TextInput(Left(aString, CursorPos)) + Mid(aString, CursorPos + 2)
				CursorPos = CursorPos + 1
			ElseIf Value = 8 ; ~ Backspace
				aString = TextInput(Left(aString, CursorPos)) + Mid(aString, CursorPos + 1)
			ElseIf Value = 4 ; ~ Delete
				aString = Left(aString, CursorPos) + Right(aString, Max(Length - CursorPos - 1, 0.0))
			EndIf
		Else
			aString = TextInput(Left(aString, CursorPos)) + Mid(aString, CursorPos + 1)
		EndIf
		CursorPos = CursorPos + Len(aString) - Length
		If MaxChr > 0 And MaxChr < Len(aString)
			aString = Left(aString, MaxChr)
			CursorPos = MaxChr
		EndIf
	EndIf
	Return(aString)
End Function

Type MenuInputBox
	Field x%, y%, Width%, Height%
	Field Txt$, FontID%
	Field ID%
End Type

Function UpdateMenuInputBox$(x%, y%, Width%, Height%, Txt$, FontID% = Font_Default, ID% = 0, MaxChr% = 0)
	Local mib.MenuInputBox, currInputBox.MenuInputBox
	Local InputBoxExists% = False
	
	For mib.MenuInputBox = Each MenuInputBox
		If mib\x = x And mib\y = y And mib\Width = Width And mib\Height = Height
			InputBoxExists = True
			Exit
		EndIf
	Next
	If (Not InputBoxExists)
		mib.MenuInputBox = New MenuInputBox
		mib\x = x
		mib\y = y
		mib\Width = Width
		mib\Height = Height
		mib\Txt = Txt
		mib\FontID = FontID
		mib\ID = ID
	Else
		currInputBox = mib
		currInputBox\Txt = Txt
		currInputBox\FontID = FontID
	EndIf
	
	Local MouseOnBox% = False
	
	If MouseOn(x, y, Width, Height)
		MouseOnBox = True
		If mo\MouseHit1
			SelectedInputBox = ID
			FlushKeys()
			CursorPos = -2
		EndIf
	EndIf
	
	If (Not MouseOnBox) And mo\MouseHit1 And SelectedInputBox = ID
		SelectedInputBox = 0
		CursorPos = -2
	EndIf
	
	If SelectedInputBox = ID Then Txt = UpdateInput(Txt, MaxChr)
	Return(Txt)
End Function

Function RenderMenuInputBoxes%()
	Local mib.MenuInputBox
	
	For mib.MenuInputBox = Each MenuInputBox
		RenderFrame(mib\x, mib\y, mib\Width, mib\Height)
		
		If MouseOn(mib\x, mib\y, mib\Width, mib\Height)
			Color(50, 50, 50)
			Rect(mib\x + (3 * MenuScale), mib\y + (3 * MenuScale), mib\Width - (6 * MenuScale), mib\Height - (6 * MenuScale))
		EndIf
		
		Color(255, 255, 255)
		If SelectedInputBox = mib\ID
			If ((MilliSec Mod 800) < 400) Lor KeyDown(205) Lor KeyDown(203) Lor InsertMode Then Rect(mib\x + (mib\Width / 2) - (StringWidth(mib\Txt) / 2) + StringWidth(Left(mib\Txt, Max(CursorPos, 0.0))), mib\y + (mib\Height / 2) - (5 * MenuScale), 2 * MenuScale, 12 * MenuScale)
		EndIf
		
		SetFontEx(fo\FontID[mib\FontID])
		TextEx(mib\x + (mib\Width / 2), mib\y + (mib\Height / 2), mib\Txt, True, True)
	Next
End Function

Function DeleteMenuInputBox%(mib.MenuInputBox)
	Delete(mib)
End Function

Type MenuSlideBar
	Field x%, y%, Width%
	Field Value#
End Type

Function UpdateMenuSlideBar#(x%, y%, Width%, Value#, ID%)
	Local msb.MenuSlideBar, currSlideBar.MenuSlideBar
	Local SlideBarExists% = False
	
	For msb.MenuSlideBar = Each MenuSlideBar
		If msb\x = x And msb\y = y And msb\Width = Width
			SlideBarExists = True
			Exit
		EndIf
	Next
	If (Not SlideBarExists)
		msb.MenuSlideBar = New MenuSlideBar
		msb\x = x
		msb\y = y
		msb\Width = Width
		msb\Value = Value
	Else
		currSlideBar = msb
		currSlideBar\Value = Value
	EndIf
	
	If mo\MouseDown1 And OnSliderID = 0
		If MouseOn(x, y, Width + (14 * MenuScale), (20 * MenuScale)) Then OnSliderID = ID
	EndIf
	If ID = OnSliderID Then Value = Min(Max((MousePosX - x) * 100 / Width, 0.0), 100.0)
	Return(Value)
End Function

Function RenderMenuSlideBars%()
	Local msb.MenuSlideBar
	
	For msb.MenuSlideBar = Each MenuSlideBar
		Local ColorR% = 255, ColorG% = 255, ColorB% = 255
		
		If MouseOn(msb\x, msb\y, msb\Width + (14 * MenuScale), 20 * MenuScale) Then ColorR = 0 : ColorG = 200 : ColorB = 0
		Color(ColorR, ColorG, ColorB)
		Rect(msb\x, msb\y, msb\Width + (14 * MenuScale), 20 * MenuScale, False)
		
		Color(200, 200, 200)
		Rect(msb\x + msb\Width * msb\Value / 100.0 + (3 * MenuScale), msb\y + (3 * MenuScale), 8, 14)
		
		Color(170, 170, 170)
		SetFontEx(fo\FontID[Font_Default])
		TextEx(msb\x - (50 * MenuScale), msb\y + (5 * MenuScale), GetLocalString("options", "slider.low"))
		TextEx(msb\x + msb\Width + (34 * MenuScale), msb\y + (5 * MenuScale), GetLocalString("options", "slider.high"))
	Next
End Function

Function DeleteMenuSlideBar%(msb.MenuSlideBar)
	Delete(msb)
End Function

Type MenuSlider
	Field x%, y%, Width%
	Field Value%
	Field ID%
	Field Val1$, Val2$, Val3$, Val4$, Val5$
	Field Amount%
End Type

Global OnSliderID%

Function UpdateMenuSlider3%(x%, y%, Width%, Value%, ID%, Val1$, Val2$, Val3$)
	Local ms.MenuSlider, currSlider.MenuSlider
	Local Slider3Exists% = False
	
	For ms.MenuSlider = Each MenuSlider
		If ms\x = x And ms\y = y And ms\Width = Width And ms\Amount = 3
			Slider3Exists = True
			Exit
		EndIf
	Next
	If (Not Slider3Exists)
		ms.MenuSlider = New MenuSlider
		ms\x = x
		ms\y = y
		ms\Width = Width
		ms\ID = ID
		ms\Value = Value
		ms\Val1 = Val1
		ms\Val2 = Val2
		ms\Val3 = Val3
		ms\Amount = 3
	Else
		currSlider = ms
		currSlider\Value = Value
	EndIf
	
	If mo\MouseDown1 And OnSliderID = 0
		If MouseOn(x, y - (8 * MenuScale), Width + (14 * MenuScale), 18 * MenuScale) Then OnSliderID = ID
	EndIf
	
	If ID = OnSliderID
		If MousePosX <= x + (8 * MenuScale)
			Value = 0
		ElseIf (MousePosX >= x + (Width / 2)) And (MousePosX <= x + (Width / 2) + (8 * MenuScale))
			Value = 1
		ElseIf MousePosX >= x + Width
			Value = 2
		EndIf
	EndIf
	Return(Value)
End Function

Function UpdateMenuSlider5%(x%, y%, Width%, Value%, ID%, Val1$, Val2$, Val3$, Val4$, Val5$)
	Local ms.MenuSlider, currSlider.MenuSlider
	Local Slider5Exists% = False
	
	For ms.MenuSlider = Each MenuSlider
		If ms\x = x And ms\y = y And ms\Width = Width And ms\Amount = 5
			Slider5Exists = True
			Exit
		EndIf
	Next
	If (Not Slider5Exists)
		ms.MenuSlider = New MenuSlider
		ms\x = x
		ms\y = y
		ms\Width = Width
		ms\ID = ID
		ms\Value = Value
		ms\Val1 = Val1
		ms\Val2 = Val2
		ms\Val3 = Val3
		ms\Val4 = Val4
		ms\Val5 = Val5
		ms\Amount = 5
	Else
		currSlider = ms
		currSlider\Value = Value
	EndIf
	
	If mo\MouseDown1 And OnSliderID = 0
		If MouseOn(x, y - (8 * MenuScale), Width + (14 * MenuScale), 18 * MenuScale) Then OnSliderID = ID
	EndIf
	
	If ID = OnSliderID
		If MousePosX <= x + (8 * MenuScale)
			Value = 0
		ElseIf (MousePosX >= x + (Width / 4)) And (MousePosX <= x + (Width / 4) + (8 * MenuScale))
			Value = 1
		ElseIf (MousePosX >= x + (Width / 2)) And (MousePosX <= x + (Width / 2) + (8 * MenuScale))
			Value = 2
		ElseIf (MousePosX >= x + (Width * 0.75)) And (MousePosX <= x + (Width * 0.75) + (8 * MenuScale))
			Value = 3
		ElseIf MousePosX >= x + Width
			Value = 4
		EndIf
	EndIf
	Return(Value)
End Function

Function RenderMenuSliders%()
	Local ms.MenuSlider
	
	For ms.MenuSlider = Each MenuSlider
		Local x1% = ms\x, y1% = ms\y - (8 * MenuScale), y2 = ms\y
		Local w1% = ms\Width + (14 * MenuScale), w2% = 4 * MenuScale
		Local h1% = 9 * MenuScale, h2% = 10 * MenuScale
		Local ColorR% = 200, ColorG% = 200, ColorB% = 200
		
		If ms\ID = OnSliderID Lor MouseOn(x1, y1, w1, h1 + h2) Then ColorR = 0 : ColorG = 200 : ColorB = 0
		
		Color(ColorR, ColorG, ColorB)
		
		Rect(x1, y2, w1, h2)
		Rect(x1, y1, w2, h1)
		
		SetFontEx(fo\FontID[Font_Default])
		If ms\Amount = 3
			Rect(x1 + (ms\Width / 2) + (5 * MenuScale), y1, w2, h1)
			Rect(x1 + ms\Width + (10 * MenuScale), y1, w2, h1)
			
			Color(170, 170, 170)
			If ms\Value = 0
				Color(200, 200, 200)
				Rect(x1, y1, 8, 14)
				TextEx(x1 + (2 * MenuScale), y2 + (12 * MenuScale), ms\Val1, True)
			ElseIf ms\Value = 1
				Color(200, 200, 200)
				Rect(x1 + (ms\Width / 2) + (3 * MenuScale), y1, 8, 14)
				TextEx(x1 + (ms\Width / 2) + (7 * MenuScale), y2 + (12 * MenuScale), ms\Val2, True)
			Else
				Color(200, 200, 200)
				Rect(x1 + ms\Width + (6 * MenuScale), y1, 8, 14)
				TextEx(x1 + ms\Width + (12 * MenuScale), y2 + (12 * MenuScale), ms\Val3, True)
			EndIf
		ElseIf ms\Amount = 5
			Rect(x1 + (ms\Width / 4) + (2.5 * MenuScale), y1, w2, h1)
			Rect(x1 + (ms\Width / 2) + (5 * MenuScale), y1, w2, h1)
			Rect(x1 + (ms\Width * 0.75) + (7.5 * MenuScale), y1, w2, h1)
			Rect(x1 + ms\Width + (10 * MenuScale), y1, w2, h1)
			
			Color(170, 170, 170)
			If ms\Value = 0
				Color(200, 200, 200)
				Rect(x1, y1, 8, 14)
				TextEx(x1 + (2 * MenuScale), y2 + (12 * MenuScale), ms\Val1, True)
			ElseIf ms\Value = 1
				Color(200, 200, 200)
				Rect(x1 + (ms\Width / 4) + (1.5 * MenuScale), y1, 8, 14)
				TextEx(x1 + (ms\Width / 4) + (4.5 * MenuScale), y2 + (12 * MenuScale), ms\Val2, True)
			ElseIf ms\Value = 2
				Color(200, 200, 200)
				Rect(x1 + (ms\Width / 2) + (3 * MenuScale), y1, 8, 14)
				TextEx(x1 + (ms\Width / 2) + (7 * MenuScale), y2 + (12 * MenuScale), ms\Val3, True)
			ElseIf ms\Value = 3
				Color(200, 200, 200)
				Rect(x1 + (ms\Width * 0.75) + (4.5 * MenuScale), y1, 8, 14)
				TextEx(x1 + (ms\Width * 0.75) + (9.5 * MenuScale), y2 + (12 * MenuScale), ms\Val4, True)
			Else
				Color(200, 200, 200)
				Rect(x1 + ms\Width + (6 * MenuScale), y1, 8, 14)
				TextEx(x1 + ms\Width + (12 * MenuScale), y2 + (12 * MenuScale), ms\Val5, True)
			EndIf
		EndIf
	Next
End Function

Function DeleteMenuSlider%(ms.MenuSlider)
	Delete(ms)
End Function

Type MenuScrollBar
	Field x%, y%
	Field Width%, Height%
	Field BarX%, BarY%
	Field BarWidth%, BarHeight%
	Field Value#
	Field Vertical%, Locked%
End Type

Global OnScrollBar%
Global ScrollBarY# = 0.0
Global ScrollMenuHeight# = 0.0

Function UpdateMenuScrollBar#(Width%, Height%, BarX%, BarY%, BarWidth%, BarHeight%, Value#, Vertical% = False, Locked% = False, Speed% = 2)
	Local msb.MenuScrollBar, currScrollBar.MenuScrollBar
	Local ScrollBarExist% = False
	
	For msb.MenuScrollBar = Each MenuScrollBar
		If msb\BarX = BarX And msb\BarY = BarY And msb\BarWidth = BarWidth And msb\BarHeight = BarHeight
			ScrollBarExist = True
			Exit
		EndIf
	Next
	If (Not ScrollBarExist)
		msb.MenuScrollBar = New MenuScrollBar
		msb\Width = Width
		msb\Height = Height
		msb\BarX = BarX
		msb\BarY = BarY
		msb\BarWidth = BarWidth
		msb\BarHeight = BarHeight
		msb\Value = Value
		msb\Vertical = Vertical
		msb\Locked = Locked
	Else
		currScrollBar = msb
		currScrollBar\Width = Width
		currScrollBar\Height = Height
		currScrollBar\Value = Value
		currScrollBar\Vertical = Vertical
		currScrollBar\Locked = Locked
	EndIf
	
	Local MouseSpeedX# = MouseXSpeed()
	Local MouseSpeedY# = MouseYSpeed()
	
	OnScrollBar = (mo\MouseDown1 And MouseOn(BarX, BarY, BarWidth, BarHeight))
	If OnScrollBar
		If mo\MouseHit1
			If Locked
				PlaySound_Strict(ButtonSFX[1])
			Else
				PlaySound_Strict(ButtonSFX[0])
			EndIf
		EndIf
		If (Not Vertical)
			Return(Min(Max(Value + MouseSpeedX / Float(Width - BarWidth), 0.0), 1.0))
		Else
			Return(Min(Max(Value + MouseSpeedY / Float(Height - BarHeight), 0.0), 1.0))
		EndIf
	EndIf
	
	Local MouseSpeedZ# = MouseZSpeed()
	
	; ~ Only for vertical scroll bars
	If MouseSpeedZ <> 0.0 Then Return(Min(Max(Value - (MouseSpeedZ * 3.0) / Float(Height - BarHeight), 0.0), 1.0))
	
	Return(Value)
End Function

Function RenderMenuScrollBars%()
	Local msb.MenuScrollBar
	
	For msb.MenuScrollBar = Each MenuScrollBar
		If OnScrollBar
			Color(30, 30, 30)
			Rect(msb\BarX, msb\BarY, msb\BarWidth, msb\BarHeight)
			Color(130, 130, 130)
			Rect(msb\BarX + 1, msb\BarY + 1, msb\BarWidth - 1, msb\BarHeight - 1, False)
			Color(10, 10, 10)
			Rect(msb\BarX, msb\BarY, msb\BarWidth, msb\BarHeight, False)
			Color(255, 255, 255)
			Line(msb\BarX, msb\BarY + msb\BarHeight - 1, msb\BarX + msb\BarWidth - 1, msb\BarY + msb\BarHeight - 1)
			Line(msb\BarX + msb\BarWidth - 1, msb\BarY, msb\BarX + msb\BarWidth - 1, msb\BarY + msb\BarHeight - 1)
		Else
			Color(100, 100, 100)
			Rect(msb\BarX, msb\BarY, msb\BarWidth, msb\BarHeight)
			Color(130, 130, 130)
			Rect(msb\BarX, msb\BarY, msb\BarWidth - 1, msb\BarHeight - 1, False)
			Color(255, 255, 255)
			Rect(msb\BarX, msb\BarY, msb\BarWidth, msb\BarHeight, False)
			Color(10, 10, 10)
			Line(msb\BarX, msb\BarY + msb\BarHeight - 1, msb\BarX + msb\BarWidth - 1, msb\BarY + msb\BarHeight - 1)
			Line(msb\BarX + msb\BarWidth - 1, msb\BarY, msb\BarX + msb\BarWidth - 1, msb\BarY + msb\BarHeight - 1)
		EndIf
		
		If (Not msb\Vertical) ; ~ Horizontal
			If msb\Height > (10 * MenuScale)
				Color(255, 255, 255)
				Rect(msb\BarX + (msb\BarWidth / 2), msb\BarY + (5 * MenuScale), 2 * MenuScale, msb\BarHeight - (10 * MenuScale))
				Rect(msb\BarX + (msb\BarWidth / 2) - (3 * MenuScale), msb\BarY + (5 * MenuScale), 2 * MenuScale, msb\BarHeight - (10 * MenuScale))
				Rect(msb\BarX + (msb\BarWidth / 2) + (3 * MenuScale), msb\BarY + (5 * MenuScale), 2 * MenuScale, msb\BarHeight - (10 * MenuScale))
			EndIf
		Else ; ~ Vertical
			If msb\Width > (10 * MenuScale)
				Color(255, 255, 255)
				Rect(msb\BarX + (4 * MenuScale), msb\BarY + (msb\BarHeight / 2), msb\BarWidth - (10 * MenuScale), 2 * MenuScale)
				Rect(msb\BarX + (4 * MenuScale), msb\BarY + (msb\BarHeight / 2) - (3 * MenuScale), msb\BarWidth - (10 * MenuScale), 2 * MenuScale)
				Rect(msb\BarX + (4 * MenuScale), msb\BarY + (msb\BarHeight / 2) + (3 * MenuScale), msb\BarWidth - (10 * MenuScale), 2 * MenuScale)
			EndIf
		EndIf
	Next
End Function

Function DeleteMenuScrollBar%(msb.MenuScrollBar)
	Delete(msb)
End Function

Function DeleteMenuGadgets%()
	Local mb.MenuButton, mp.MenuPalette, mt.MenuTick, mib.MenuInputBox, msb.MenuSlideBar, mscb.MenuScrollBar, ms.MenuSlider;, msb.MenuScrollBar
	
	For mb.MenuButton = Each MenuButton
		DeleteMenuButton(mb)
	Next
	For mp.MenuPalette = Each MenuPalette
		DeleteMenuPallete(mp)
	Next
	For mt.MenuTick = Each MenuTick
		DeleteMenuTick(mt)
	Next
	For mib.MenuInputBox = Each MenuInputBox
		DeleteMenuInputBox(mib)
	Next
	For msb.MenuSlideBar = Each MenuSlideBar
		DeleteMenuSlideBar(msb)
	Next
	For mscb.MenuScrollBar = Each MenuScrollBar
		DeleteMenuScrollBar(mscb)
	Next
	For ms.MenuSlider = Each MenuSlider
		DeleteMenuSlider(ms)
	Next
End Function

Function RowText%(Txt$, x%, y%, W%, H%, Align% = False, Leading# = 1.0)
	; ~ Display A$ starting at x, y - no wider than W and no taller than H (all in pixels)
	; ~ Leading is optional extra vertical spacing in pixels
	
	If H < 1 Then H = SMALLEST_POWER_TWO
	
	Local LinesShown% = 0
	Local Height% = StringHeight(Txt) + Leading
	Local s$
	
	While Len(Txt) > 0
		Local Space% = Instr(Txt, SplitSpace)
		
		If Space = 0 Then Space = Len(Txt)
		
		Local Temp$ = Left(Txt, Space)
		Local Trimmed$ = Trim(Temp) ; ~ We might ignore a final space 
		Local Extra% = 0 ; ~ We haven't ignored it yet
		
		; ~ Ignore final space if doing so would make a word fit at end of line:
		If StringWidth(s + Temp) > W And StringWidth(s + Trimmed) <= W
			Temp = Trimmed
			Extra = 1
		EndIf
		
		If StringWidth(s + Temp) > W ; ~ Too big, so print what will fit
			If Align
				TextEx(x + (W / 2) - (StringWidth(s) / 2), y + (LinesShown * Height), s)
			Else
				TextEx(x, y + (LinesShown * Height), s)
			EndIf
			
			LinesShown = LinesShown + 1
			s = ""
		Else ; ~ Append it to b$ (which will eventually be printed) and remove it from A$
			s = s + Temp
			Txt = Right(Txt, Len(Txt) - (Len(Temp) + Extra))
		EndIf
		If ((LinesShown + 1) * Height) > H Then Exit ; ~ The next line would be too tall, so leave
	Wend
	
	If s <> "" And (LinesShown + 1) <= H
		If Align
			TextEx(x + (W / 2) - (StringWidth(s) / 2), y + (LinesShown * Height), s) ; ~ Print any remaining text if it'll fit vertically
		Else
			TextEx(x, y + (LinesShown * Height), s) ; ~ Print any remaining text if it'll fit vertically
		EndIf
	EndIf
End Function

Function GetLineAmount%(Txt$, W%, H%, Leading# = 1.0)
	; ~ Display A$ no wider than W and no taller than H (all in pixels)
	; ~ Leading is optional extra vertical spacing in pixels
	
	If H < 1 Then H = SMALLEST_POWER_TWO
	
	Local LinesShown% = 0
	Local Height% = StringHeight(Txt) + Leading
	Local s$
	
	While Len(Txt) > 0
		Local Space% = Instr(Txt, SplitSpace)
		
		If Space = 0 Then Space = Len(Txt)
		
		Local Temp$ = Left(Txt, Space)
		Local Trimmed$ = Trim(Temp) ; ~ We might ignore a final space
		Local Extra% = 0 ; ~ We haven't ignored it yet
		
		; ~ Ignore final space if doing so would make a word fit at end of line:
		If StringWidth(s + Temp) > W And StringWidth(s + Trimmed) <= W
			Temp = Trimmed
			Extra = 1
		EndIf
		
		If StringWidth(s + Temp) > W ; ~ Too big, so print what will fit
			LinesShown = LinesShown + 1
			s = ""
		Else ; ~ Append it to b$ (which will eventually be printed) and remove it from A$
			s = s + Temp
			Txt = Right(Txt, Len(Txt) - (Len(Temp) + Extra))
		EndIf
		If ((LinesShown + 1) * Height) > H Then Exit ; ~ The next line would be too tall, so leave
	Wend
	Return(LinesShown + 1)
End Function

; ~ Graphics Tooltips Constants
;[Block]
Const Tooltip_BumpMapping% = 0
Const Tooltip_VSync% = 1
Const Tooltip_AntiAliasing% = 2
Const Tooltip_RoomLights% = 3
Const Tooltip_ScreenGamma% = 4
Const Tooltip_TextureLODBias% = 5
Const Tooltip_ParticleAmount% = 6
Const Tooltip_SaveTexturesInVRAM% = 7
Const Tooltip_FOV% = 8
Const Tooltip_AnisotropicFiltering% = 9
Const Tooltip_Atmosphere% = 10
Const Tooltip_SecurityCamRenderInterval% = 11
;[End Block]

; ~ Audio Tooltips Constants
;[Block]
Const Tooltip_MasterVolume% = 12
Const Tooltip_MusicVolume% = 13
Const Tooltip_SoundVolume% = 14
Const Tooltip_VoiceVolume% = 15
Const Tooltip_SoundAutoRelease% = 16
Const Tooltip_UserTracks% = 17
Const Tooltip_UserTracksMode% = 18
Const Tooltip_UserTrackScan% = 19
;[End Block]

; ~ Controls Tooltips Constants
;[Block]
Const Tooltip_MouseSensitivity% = 20
Const Tooltip_MouseInvertX% = 21
Const Tooltip_MouseInvertY% = 22
Const Tooltip_MouseSmoothing% = 23
Const Tooltip_ControlConfiguration% = 24
;[End Block]

; ~ Advanced Tooltips Constants
;[Block]
Const Tooltip_HUD% = 25
Const Tooltip_Console% = 26
Const Tooltip_ConsoleOnError% = 27
Const Tooltip_AchievementPopups% = 28
Const Tooltip_FPS% = 29
Const Tooltip_FrameLimit% = 30
Const Tooltip_AutoSave% = 31
Const Tooltip_TextShadow% = 32
Const Tooltip_SmoothBars% = 33
Const Tooltip_StartupVideos% = 34
Const Tooltip_Launcher% = 35
Const Tooltip_Subtitles% = 36
Const Tooltip_SubtitlesColor% = 37
Const Tooltip_ResetOptions% = 38
;[End Block]

Function RenderOptionsTooltip%(x%, y%, Width%, Height%, Option%, Value# = 0.0)
	Local fX# = x + (6.0 * MenuScale)
	Local fY# = y + (6.0 * MenuScale)
	Local fW# = Width - (12.0 * MenuScale)
	Local fH# = Height - (12.0 * MenuScale)
	Local Lines% = 0, Lines2% = 0
	Local Txt$ = "", Txt2$ = ""
	Local R% = 0, G% = 0, B% = 0
	
	SetFontEx(fo\FontID[Font_Default])
	Color(255, 255, 255)
	Select(Lower(Option))
			; ~ [GRAPHICS]
		Case Tooltip_BumpMapping
			;[Block]
			Txt = GetLocalString("tooltip", "bump")
			R = 255
			Txt2 = GetLocalString("tooltip", "cantchange")
			;[End Block]
		Case Tooltip_VSync
			;[Block]
			Txt = GetLocalString("tooltip", "vsync")
			;[End Block]
		Case Tooltip_AntiAliasing
			;[Block]
			Txt = GetLocalString("tooltip", "alias")
			R = 255 : G = 255
			Txt2 = GetLocalString("tooltip", "fullscreen")
			;[End Block]
		Case Tooltip_RoomLights
			;[Block]
			Txt = GetLocalString("tooltip", "lights")
			;[End Block]
		Case Tooltip_ScreenGamma
			;[Block]
			Txt = GetLocalString("tooltip", "gamma")
			R = 255 : G = 255
			Txt2 = Format(GetLocalString("tooltip", "default.value.100"), Int(Value * 100.0))
			;[End Block]
		Case Tooltip_ParticleAmount
			;[Block]
			Txt = GetLocalString("tooltip", "particle_1")
			Select(Value)
				Case 0
					;[Block]
					R = 255
					Txt2 = GetLocalString("tooltip", "particle_2.1")
					;[End Block]
				Case 1
					;[Block]
					R = 255
					G = 255
					Txt2 = GetLocalString("tooltip", "particle_2.2")
					;[End Block]
				Case 2
					;[Block]
					G = 255
					Txt2 = GetLocalString("tooltip", "particle_2.3")
					;[End Block]
			End Select
			;[End Block]
		Case Tooltip_TextureLODBias
			;[Block]
			Txt = GetLocalString("tooltip", "lod")
			;[End Block]
		Case Tooltip_SaveTexturesInVRAM
			;[Block]
			Txt = GetLocalString("tooltip", "vram")
			R = 255
			Txt2 = GetLocalString("tooltip", "cantchange")
			;[End Block]
		Case Tooltip_FOV
			;[Block]
			Txt = GetLocalString("tooltip", "fov")
			R = 255 : G = 255
			Txt2 = Format(GetLocalString("tooltip", "default.value.fov"), Int(opt\FOV))
			;[End Block]
		Case Tooltip_AnisotropicFiltering
			;[Block]
			Txt = GetLocalString("tooltip", "anisotropic")
			;[End Block]
		Case Tooltip_Atmosphere
			;[Block]
			Txt = GetLocalString("tooltip", "atmo")
			R = 255
			Txt2 = GetLocalString("tooltip", "cantchange")
			;[End Block]
		Case Tooltip_SecurityCamRenderInterval
			;[Block]
			Txt = GetLocalString("tooltip", "screnderinterval")
			;[End Block]
			; ~ [AUDIO]
		Case Tooltip_MasterVolume
			;[Block]
			Txt = GetLocalString("tooltip", "mastervolume")
			R = 255 : G = 255
			Txt2 = Format(GetLocalString("tooltip", "default.value.50"), Int(Value * 100.0))
			;[End Block]
		Case Tooltip_MusicVolume
			;[Block]
			Txt = GetLocalString("tooltip", "musicvolume")
			R = 255 : G = 255
			Txt2 = Format(GetLocalString("tooltip", "default.value.50"), Int(Value * 100.0))
			;[End Block]
		Case Tooltip_SoundVolume
			;[Block]
			Txt = GetLocalString("tooltip", "soundvolume")
			R = 255 : G = 255
			Txt2 = Format(GetLocalString("tooltip", "default.value.50"), Int(Value * 100.0))
			;[End Block]
		Case Tooltip_VoiceVolume
			;[Block]
			Txt = GetLocalString("tooltip", "voicevolume")
			R = 255 : G = 255
			Txt2 = Format(GetLocalString("tooltip", "default.value.50"), Int(Value * 100.0))
			;[End Block]
		Case Tooltip_SoundAutoRelease
			;[Block]
			Txt = GetLocalString("tooltip", "autorelease")
			R = 255
			Txt2 = GetLocalString("tooltip", "cantchange")
			;[End Block]
		Case Tooltip_UserTracks
			;[Block]
			Txt = GetLocalString("tooltip", "usertrack")
			R = 255
			Txt2 = GetLocalString("tooltip", "cantchange")
			;[End Block]
		Case Tooltip_UserTracksMode
			;[Block]
			Txt = GetLocalString("tooltip", "trackmode")
			R = 255 : G = 255
			Txt2 = GetLocalString("tooltip", "modenote")
			;[End Block]
		Case Tooltip_UserTrackScan
			;[Block]
			Txt = GetLocalString("tooltip", "scantrack")
			R = 255
			Txt2 = GetLocalString("tooltip", "cantchangebtn")
			;[End Block]
		Case Tooltip_Subtitles
			;[Block]
			Txt = GetLocalString("tooltip", "subtitles")
			;[End Block]
		Case Tooltip_SubtitlesColor
			;[Block]
			Txt = GetLocalString("tooltip", "subtitles.color")
			;[End Block]
			; ~ [CONTROLS]
		Case Tooltip_MouseSensitivity
			;[Block]
			Txt = GetLocalString("tooltip", "mousespeed")
			R = 255 : G = 255
			Txt2 = Format(GetLocalString("tooltip", "default.value.0"), Int(Value * 100.0))
			;[End Block]
		Case Tooltip_MouseInvertX
			;[Block]
			Txt = GetLocalString("tooltip", "invertx")
			;[End Block]
		Case Tooltip_MouseInvertY
			;[Block]
			Txt = GetLocalString("tooltip", "inverty")
			;[End Block]
		Case Tooltip_MouseSmoothing
			;[Block]
			Txt = GetLocalString("tooltip", "mousesmooth")
			R = 255 : G = 255
			Txt2 = Format(GetLocalString("tooltip", "default.value.100"), Int(Value * 100.0))
			;[End Block]
		Case Tooltip_ControlConfiguration
			;[Block]
			Txt = GetLocalString("tooltip", "configcontrol")
			;[End Block]
			; ~ [ADVANCED]
		Case Tooltip_HUD
			;[Block]
			Txt = GetLocalString("tooltip", "hud")
			;[End Block]
		Case Tooltip_Console
			;[Block]
			Txt = Format(GetLocalString("tooltip", "console"), key\Name[key\CONSOLE])
			;[End Block]
		Case Tooltip_ConsoleOnError
			;[Block]
			Txt = GetLocalString("tooltip", "errorconsole")
			;[End Block]
		Case Tooltip_AchievementPopups
			;[Block]
			Txt = GetLocalString("tooltip", "achipop")
			;[End Block]
		Case Tooltip_AutoSave
			;[Block]
			Txt = Format(GetLocalString("tooltip", "autosave"), key\Name[key\SAVE])
			R = 255 : G = 255
			Txt2 = GetLocalString("tooltip", "autosave.note")
			;[End Block]
		Case Tooltip_TextShadow
			;[Block]
			Txt = GetLocalString("tooltip", "txtshadow")
			;[End Block]
		Case Tooltip_FPS
			;[Block]
			Txt = GetLocalString("tooltip", "fps")
			;[End Block]
		Case Tooltip_FrameLimit
			;[Block]
			Txt = GetLocalString("tooltip", "frame")
			If Value > 0 And Value < 60
				R = 255 : G = 255
				Txt2 = GetLocalString("tooltip", "frame.note")
			EndIf
			;[End Block]
		Case Tooltip_SmoothBars
			;[Block]
			Txt = GetLocalString("tooltip", "bar")
			;[End Block]
		Case Tooltip_StartupVideos
			;[Block]
			Txt = GetLocalString("tooltip", "startvideo")
			;[End Block]
		Case Tooltip_Launcher
			;[Block]
			Txt = GetLocalString("tooltip", "launcher")
			;[End Block]
		Case Tooltip_ResetOptions
			;[Block]
			Txt = GetLocalString("tooltip", "reset")
			R = 255
			Txt2 = GetLocalString("tooltip", "cantchangebtn")
			;[End Block]
	End Select
	
	Lines = GetLineAmount(Txt, fW, fH)
	If Txt2 = ""
		RenderFrame(x, y, Width, ((StringHeight(Txt) * Lines) + (10 + Lines) * MenuScale))
	Else
		Lines2 = GetLineAmount(Txt2, fW, fH)
		RenderFrame(x, y, Width, (((StringHeight(Txt) * Lines) + (10 + Lines) * MenuScale) + (StringHeight(Txt2) * Lines2) + (10 + Lines2) * MenuScale))
	EndIf
	RowText(Txt, fX, fY, fW, fH)
	If Txt2 <> ""
		Color(R, G, B)
		RowText(Txt2, fX, (fY + (StringHeight(Txt) * Lines) + (5 + Lines) * MenuScale), fW, fH)
		Color(255, 255, 255)
	EndIf
End Function

Function RenderMapCreatorTooltip%(x%, y%, Width%, Height%, MapName$)
	Local fX# = x + (6.0 * MenuScale)
	Local fY# = y + (6.0 * MenuScale)
	Local fW# = Width - (12.0 * MenuScale)
	Local fH# = Height - (12.0 * MenuScale)
	Local Lines% = 0, Temp%
	
	SetFontEx(fo\FontID[Font_Default])
	Color(255, 255, 255)
	
	Local Txt$[6]
	
	If Right(MapName, 6) = "cbmap2"
		Txt[0] = Left(ConvertToUTF8(MapName), Len(ConvertToUTF8(MapName)) - 7)
		
		Local f% = OpenFile_Strict(CustomMapsPath + MapName)
		Local Author$ = ConvertToUTF8(ReadLine(f))
		Local Descr$ = ConvertToUTF8(ReadLine(f))
		
		ReadByte(f)
		ReadByte(f)
		
		Local rAmount% = ReadInt(f)
		Local HasForest%, HasMT%
		
		HasForest = (ReadInt(f) > 0)
		HasMT = (ReadInt(f) > 0)
		
		CloseFile(f)
	Else
		Txt[0] = Left(MapName, Len(MapName) - 6)
		Author = GetLocalString("creator", "unknown")
		Descr = GetLocalString("creator", "nodesc")
		rAmount = 0
		HasForest = False
		HasMT = False
	EndIf
	Txt[1] = Format(GetLocalString("creator", "author"), Author)
	Txt[2] = Format(GetLocalString("creator", "desc"), Descr)
	If rAmount > 0
		Txt[3] = Format(GetLocalString("creator", "ramount"), rAmount)
	Else
		Txt[3] = Format(GetLocalString("creator", "ramount"), GetLocalString("creator", "unknown"))
	EndIf
	If HasForest
		Txt[4] = Format(GetLocalString("creator", "forest"), GetLocalString("creator", "yes"))
	Else
		Txt[4] = Format(GetLocalString("creator", "forest"), GetLocalString("creator", "no"))
	EndIf
	If HasMT
		Txt[5] = Format(GetLocalString("creator", "mt"), GetLocalString("creator", "yes"))
	Else
		Txt[5] = Format(GetLocalString("creator", "mt"), GetLocalString("creator", "no"))
	EndIf
	
	Lines = GetLineAmount(Txt[2], fW, fH)
	RenderFrame(x, y, Width, (StringHeight(Txt[0]) * 6) + StringHeight(Txt[2]) * Lines + (5 * MenuScale))
	
	Color(255, 255, 255)
	TextEx(fX, fY,Txt[0])
	TextEx(fX, fY + StringHeight(Txt[0]), Txt[1])
	RowText(Txt[2], fX, fY + (StringHeight(Txt[0]) * 2), fW, fH)
	TextEx(fX, fY + ((StringHeight(Txt[0]) * 2) + StringHeight(Txt[2]) * Lines + (5 * MenuScale)), Txt[3])
	TextEx(fX, fY + ((StringHeight(Txt[0]) * 3) + StringHeight(Txt[2]) * Lines + (5 * MenuScale)), Txt[4])
	TextEx(fX, fY + ((StringHeight(Txt[0]) * 4) + StringHeight(Txt[2]) * Lines + (5 * MenuScale)), Txt[5])
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS