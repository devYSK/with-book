using Microsoft.EntityFrameworkCore;
using Prometheus;
using System.Threading.Tasks;
using ToDoList.Entities;
using ToDoList.Messaging;
using ToDoList.Messaging.Messages.Events;
using ToDoList.Model;

namespace ToDoList.Services
{
    public class ToDoService
    {
        private static readonly Counter _NewTasksCounter = Metrics.CreateCounter("todo_tasks_created_total", "TODO List - Number of Tasks created");

        private readonly ToDoContext _context;

        public ToDoService(ToDoContext context)
        {
            _context = context;
        }

        public async Task<ToDo[]> GetToDosAsync()
        {
            return await _context.ToDos.ToArrayAsync();
        }

        public async Task<int> GetToDoCountAsync()
        {
            return await _context.ToDos.CountAsync();
        }

        public void AddToDo(ToDo todo)
        {
            MessageQueue.Publish(new NewItemEvent(todo));
            _NewTasksCounter.Inc();
        }
    }
}
