using System;
using UnityEngine;

public class SatelliteScript : MonoBehaviour
{
    public float speed = 1.2f;
    public float width = 200;
    public float range = 50;
    public float interval;
    private float _timer;
    private float _timeCounter = 0;
    private GameObject _player;
    private AudioSource _audioSource;

    public GameObject LaserBullet;
    
    private void Start() {
        _player = GameObject.Find("Player");
        _audioSource = GetComponent<AudioSource>();
        _timer = interval;
    }
    
    private void Update() {
        _timeCounter += Time.deltaTime * speed;
        float x = Mathf.Cos(_timeCounter) * width;
        float y = transform.position.y;
        float z = Mathf.Sin(_timeCounter) * width;
        transform.position = new Vector3(x, y, z);
        
        if (Vector3.Distance(_player.transform.position, transform.position) <= range) {
            Quaternion tempVec = transform.rotation;
            transform.LookAt(_player.transform);
            transform.Rotate(new Vector3(0,1,1), 180); 
            Quaternion tempVec2 = transform.rotation;
            transform.rotation = tempVec;
            transform.rotation = Quaternion.Lerp(transform.rotation, tempVec2, Time.deltaTime * 5);
            _timer -= Time.deltaTime;
            if (_timer <= 0) {
                Instantiate(LaserBullet, transform.position, transform.rotation);
                _audioSource.PlayDelayed(0);
                _timer = interval;
            }
        } else {
            Quaternion tempVec = transform.rotation;
            transform.Rotate(new Vector3(90,90,0));
            Quaternion tempVec2 = transform.rotation;
            transform.rotation = tempVec;
            transform.rotation = Quaternion.Lerp(transform.rotation, tempVec2, Time.deltaTime);
            
        }
    }
}
