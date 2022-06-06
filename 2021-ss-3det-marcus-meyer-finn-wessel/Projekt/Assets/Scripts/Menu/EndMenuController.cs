using System;
using System.Collections;
using System.Collections.Generic;
using DefaultNamespace.HighScores;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class EndMenuController : MonoBehaviour {
    
    private Text[] _text = new Text[3];
    //private TimeSpan _totalTime;
    private int _totalTimeInSeconds;
    private string _playerName;
    private Button _insertHighScoreButton;

    private GameObject _addToHighScores;
    private GameObject _endMenu;
    
    void Start()
    {
        _addToHighScores = transform.Find("AddToHighScores").gameObject;
        _endMenu = transform.Find("EndMenu").gameObject;
        HideAddHighScore();
        Cursor.lockState = CursorLockMode.None;
        _playerName = null;
        _insertHighScoreButton = transform.Find("AddToHighScores/InsertHighScoreButton/InsertHighScoreButton").GetComponent<Button>();
        _insertHighScoreButton.interactable = false;
        int minutes = 0;
        int seconds = 0;
        for (int i = 0; i < _text.Length; i++) {
            _text[i] = transform.Find("EndMenu/Time_" + i).GetComponent<Text>();
            TimeSpan t = GameManager.Instance.ClearTime[i];
            _text[i].text = $"Level {i + 1}: {t.Minutes}:{t.Seconds}:{t.Milliseconds}";
            minutes = minutes + t.Minutes;
            seconds = seconds + t.Seconds;
        }

        _totalTimeInSeconds = (minutes * 60) + seconds;
    }

    public void SaveHighScore()
    {
        HighScores scores = loadHighScores();
        scores.HighScoreEntriesList.Add(new HighScoreEntry{timeInSeconds = _totalTimeInSeconds, name = _playerName});
        string json = JsonUtility.ToJson(scores);
        PlayerPrefs.SetString("highScoreTable", json);
        PlayerPrefs.Save();
        HideAddHighScore();
    }

    public void OnNameEnter(string name)
    {
        _playerName = name;
        if (_playerName.Length >= 3)
        {
            _insertHighScoreButton.interactable = true;
        }
        else
        {
            _insertHighScoreButton.interactable = false;
        }
    }

    public void ShowAddHighScore()
    {
        _endMenu.SetActive(false);
        _addToHighScores.SetActive(true);
    }
    
    public void HideAddHighScore()
    {
        _endMenu.SetActive(true);
        _addToHighScores.SetActive(false);
    }

    private HighScores loadHighScores()
    {
        string jsonString = PlayerPrefs.GetString("highScoreTable");
        HighScores highScores = JsonUtility.FromJson<HighScores>(jsonString);
        return highScores;
    }
    public void Menu() {
        GameManager.Instance.ToMenu();
    }

    public void CloseGame() {
        Application.Quit();
    }
}
