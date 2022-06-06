using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CrocodileController : MonoBehaviour
{
    private void OnCollisionEnter(Collision other) {
        if (other.gameObject.name == "Player") {
            GameManager.Instance.DecLife(0.25f);
        }
    }
}
