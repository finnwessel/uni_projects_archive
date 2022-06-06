using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class LevelManager : MonoBehaviour
{
    private static LevelManager _instance = null;

    public static LevelManager Instance => _instance;

    public GameObject SpaceStationStruts;

    private StationRotator _spaceStationStrutsScript;

    public void Awake() {
        if (_instance == null)
        {
            _instance = this;
        }
    }
    
    void Start()
    {
        _spaceStationStrutsScript = SpaceStationStruts.GetComponent<StationRotator>();
    }

    public void SwitchPressed()
    {
        GameManager.Instance.IncObj();
        if (GameManager.Instance.ObjectiveCounter > 3)
        {
            _spaceStationStrutsScript.RotationEnabled = false;
        }  
        if (GameManager.Instance.ObjectiveCounter > 4)
        {
            GameManager.Instance.Won();
        } 
    }
}
