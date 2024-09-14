// ch29/ex29.4/ex29.4.go
package main

import (
	"net/http"
	"sort"
	"strconv"

	"github.com/gin-gonic/gin"
)

type Student struct {
	Id    int    `json:"id,omitempty"`
	Name  string `json:"name"`
	Age   int    `json:"age"`
	Score int    `json:"score,omitempty"`
}

var students map[int]Student // ❶ 학생 목록을 저장하는 맵
var lastId int

func SetupHandlers(g *gin.Engine) {
	g.GET("/students", GetStudentsHandler)
	g.GET("/student/:id", GetStudentHandler)
	g.POST("/student", PostStudentHandler)
	g.DELETE("/student/:id", DeleteStudentHandler)

	students = make(map[int]Student) // ❹ 임시 데이터 생성
	students[1] = Student{1, "aaa", 16, 87}
	students[2] = Student{2, "bbb", 18, 98}
	lastId = 2
}

type Students []Student // Id로 정렬하는 인터페이스
func (s Students) Len() int {
	return len(s)
}
func (s Students) Swap(i, j int) {
	s[i], s[j] = s[j], s[i]
}
func (s Students) Less(i, j int) bool {
	return s[i].Id < s[j].Id
}

func GetStudentsHandler(c *gin.Context) {
	list := make(Students, 0) // ➎ 학생 목록을 Id로 정렬
	for _, student := range students {
		list = append(list, student)
	}

	sort.Sort(list)
	c.JSON(http.StatusOK, list)
}

func GetStudentHandler(c *gin.Context) {
	idstr := c.Param("id")
	if idstr == "" {
		c.AbortWithStatus(http.StatusBadRequest)
		return
	}
	id, err := strconv.Atoi(idstr)
	if err != nil {
		c.JSON(http.StatusBadRequest, err.Error())
		return
	}
	student, ok := students[id]
	if !ok {
		c.AbortWithStatus(http.StatusNotFound) // ❷ id에 해당하는 학생이 없으면 에러
		return
	}
	c.JSON(http.StatusOK, student)
}

func PostStudentHandler(c *gin.Context) {
	var student Student
	if err := c.ShouldBindJSON(&student); err != nil {
		c.JSON(http.StatusBadRequest, err.Error())
		return
	}
	lastId++ // ❷ id를 증가시킨후 맵에 등록
	student.Id = lastId
	students[lastId] = student
	c.String(http.StatusCreated, "Success to add id:%d", lastId)
}

func DeleteStudentHandler(c *gin.Context) {
	idstr := c.Param("id")
	if idstr == "" {
		c.AbortWithStatus(http.StatusBadRequest)
		return
	}
	id, err := strconv.Atoi(idstr)
	if err != nil {
		c.JSON(http.StatusBadRequest, err.Error())
		return
	}
	delete(students, id)
	c.String(http.StatusOK, "success to delete")
}

func main() {
	r := gin.Default()
	SetupHandlers(r)
	r.Run(":3000")
}
