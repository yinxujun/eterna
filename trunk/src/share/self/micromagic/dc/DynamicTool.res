# Copyright 2009-2015 xinjunli (micromagic@sina.com).
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# @author micromagic@sina.com
#
# �������Ķ���
## methodProxy.invoke.declare
public Object invoke(Object target, Object[] args)
		throws Throwable

# ���Ŀ����������
## methodProxy.check.target
if (target == null)
{
	throw new NullPointerException("The param target is null.");
}
if (!(target instanceof ${type}))
{
	throw new IllegalArgumentException("The param target (" + target.getClass() + ") isn't wanted type.");
}

# ���Ŀ������ĸ���
## methodProxy.check.args
if (args == null)
{
	throw new NullPointerException("The param args is null.");
}
if (args.length != ${paramCount})
{
	throw new IllegalArgumentException("Wrong number of arguments " + args.length + ", wanted ${paramCount}.");
}

# Ŀ�귽���ĵ��� �޷���ֵ
## methodProxy.doInvoke.void
${target}.${method}(${params});
return null;

# Ŀ�귽���ĵ��� ��������
## methodProxy.doInvoke.primitive
return new ${wrapType}(${target}.${method}(${params}));

# Ŀ�귽���ĵ���
## methodProxy.doInvoke
return ${target}.${method}(${params});

# ������ǿ������ת�� ������
## methodProxy.param.cast.withDeclare
${type} param${index} = (${type}) args[${index}];

# ������ǿ������ת�� �����
## methodProxy.param.cast.withCheck
if (args[${index}] != null && !(args[${index}] instanceof ${type}))
{
	throw new IllegalArgumentException("The arg${index} (" + args[${index}].getClass() + ") isn't wanted type.");
}
${type} param${index} = (${type}) args[${index}];

# ������ǿ������ת�� �������� ������
## methodProxy.param.cast.primitive.withDeclare
${type} param${index} = ((${wrapType}) args[${index}]).${type}Value();

# ������ǿ������ת�� �������� �����
## methodProxy.param.cast.primitive.withCheck
if (args[${index}] == null)
{
	throw new NullPointerException("The arg${index} is null.");
}
if (!(args[${index}] instanceof ${wrapType}))
{
	throw new IllegalArgumentException("The arg${index} (" + args[${index}].getClass() + ") isn't wanted type.");
}
${type} param${index} = ((${wrapType}) args[${index}]).${type}Value();