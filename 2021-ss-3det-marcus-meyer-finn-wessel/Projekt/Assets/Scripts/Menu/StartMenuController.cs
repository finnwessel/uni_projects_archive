using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class StartMenuController : MonoBehaviour
{

    private GameObject _highScoreUi;
    private GameObject _startMenuUi;
    private GameObject _settingsUi;
    private void Start() {
        Cursor.lockState = CursorLockMode.None;
        _highScoreUi = transform.Find("Background/HighScores").gameObject;
        _startMenuUi = transform.Find("Background/StartMenu").gameObject;
        _settingsUi = transform.Find("Background/SettingsMenu").gameObject;
        ShowStartMenu();
    }

    public void ShowStartMenu()
    {
        _highScoreUi.SetActive(false);
        _startMenuUi.SetActive(true);
        _settingsUi.SetActive(false);
    }

    public void ShowHighScores()
    {
        _highScoreUi.SetActive(true);
        _startMenuUi.SetActive(false);
        _settingsUi.SetActive(false);
    }
    
    public void ShowSettingsMenu()
    {
        _highScoreUi.SetActive(false);
        _startMenuUi.SetActive(false);
        _settingsUi.SetActive(true);
    }
    
    public void StartGame() {
        GameManager.Instance.StartGame();
    }
    
    public void CloseGame() {
        Application.Quit();
    }
}
