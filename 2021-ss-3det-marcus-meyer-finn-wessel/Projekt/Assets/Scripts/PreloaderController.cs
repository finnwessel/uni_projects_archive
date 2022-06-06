using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PreloaderController : MonoBehaviour
{
    private void Awake()
    {
        if (GameObject.Find("GameManager") == null)
        {
            GameManager.Load();
        }
    }
}
