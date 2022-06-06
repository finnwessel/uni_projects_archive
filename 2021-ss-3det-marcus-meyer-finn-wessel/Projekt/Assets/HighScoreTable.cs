using System;
using System.Collections;
using System.Collections.Generic;
using DefaultNamespace.HighScores;
using UnityEngine;
using UnityEngine.UI;

public class HighScoreTable : MonoBehaviour
{
    private Transform _entryContainer;
    private Transform _entryTemplate;
    private float templateHeight = 40f;
    private List<HighScoreEntry> _highScoreEntryList;
    private List<Transform> _highScoreEntryTransformList;
    private void Awake()
    {
        _entryContainer = transform.Find("HighScoreEntryContainer");
        _entryTemplate = _entryContainer.Find("HighScoreEntryTemplate");
        _entryTemplate.gameObject.SetActive(false);
        
        loadHighScoreList();
        
        _highScoreEntryList.Sort((x, y) => (x.timeInSeconds < y.timeInSeconds) ? -1: 1);

        _highScoreEntryTransformList = new List<Transform>();
        foreach (var highScoreEntry in _highScoreEntryList)
        {
            CreateHighScoreEntryTransform(highScoreEntry, _entryContainer, _highScoreEntryTransformList);
        }
    }

    private void loadHighScoreList()
    {
        string jsonString = PlayerPrefs.GetString("highScoreTable");
        HighScores highScores = JsonUtility.FromJson<HighScores>(jsonString);
        if (highScores == null)
        {
            highScores = createHighScoreList();
        }
        _highScoreEntryList = highScores.HighScoreEntriesList;
    }

    private HighScores createHighScoreList()
    {
        HighScores scores = new HighScores {HighScoreEntriesList = _highScoreEntryList};
        string json = JsonUtility.ToJson(scores);
        PlayerPrefs.SetString("highScoreTable", json);
        PlayerPrefs.Save();
        return scores;
    }

    private void CreateHighScoreEntryTransform(HighScoreEntry highScoreEntry, Transform container, List<Transform> transformList) 
    {
        Transform entryTransform = Instantiate(_entryTemplate, container);
        RectTransform entryRectTransform = entryTransform.GetComponent<RectTransform>();
        entryRectTransform.anchoredPosition = new Vector2(0, -templateHeight * transformList.Count);

        int rank = transformList.Count + 1;
        string rankString = rank switch
        {
            1 => "1ST",
            2 => "2ND",
            3 => "3RD",
            _ => rank + "TH"
        };
        entryTransform.Find("PosText").GetComponent<Text>().text = rankString;
        entryTransform.Find("TimeText").GetComponent<Text>().text =
            $"{highScoreEntry.timeInSeconds / 60}:{highScoreEntry.timeInSeconds % 60} min";
        entryTransform.Find("NameText").GetComponent<Text>().text = highScoreEntry.name;
        entryTransform.gameObject.SetActive(true);
        entryTransform.Find("Background").gameObject.SetActive(rank % 2 == 1);
        transformList.Add(entryTransform);
    }
}
